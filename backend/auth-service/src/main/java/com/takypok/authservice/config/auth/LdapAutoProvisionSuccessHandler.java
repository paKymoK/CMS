package com.takypok.authservice.config.auth;

import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.model.event.AccountEvent;
import com.takypok.authservice.repository.UserinfoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * On a first-time LDAP login, auto-provisions a disabled local shadow row in Spring Security's own
 * {@code users} table. That row backs group membership and role assignment FKs (an authorization
 * concern) — unrelated to HR data, so it survives even though employee-service now owns the profile
 * record that used to gate login here.
 *
 * <p>First-time-seen is also the moment this sub becomes "real" for an internal/LDAP user — nobody
 * provisions their login through auth-service, LDAP already did. So this is the hook that tells
 * employee-service it's safe to create a shell record for them. It's also the moment auth-service
 * captures {@code displayName}/{@code mail} from the directory into its own {@link Userinfo} record
 * (Phase 7) — LDAP already has this data, so there's no reason to ask for it twice.
 *
 * <p>This is also the single success handler for every {@code /login}, both domains — so it's the
 * natural hook for the guest "complete profile" gate too: if a GUEST login's {@link Userinfo} has
 * no name yet, redirect to {@code /profile/complete} instead of resuming the original request,
 * leaving it parked in the session's {@code RequestCache} until the guest submits their name.
 */
@Slf4j
@RequiredArgsConstructor
public class LdapAutoProvisionSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private final JdbcUserDetailsManager userDetailsManager;
  private final PasswordEncoder passwordEncoder;
  private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
  private final String accountEventsTopic;
  private final UserinfoRepository userinfoRepository;
  private final LdapTemplate ldapTemplate;
  private final String userSearchBase;
  private final String userSearchFilter;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    String username = authentication.getName();
    if (!userDetailsManager.userExists(username)) {
      // The password is a random bcrypt hash — it can never be used for JDBC login.
      var user =
          User.builder()
              .username(username)
              .password(passwordEncoder.encode(UUID.randomUUID().toString()))
              .disabled(true)
              .roles("USER")
              .build();
      userDetailsManager.createUser(user);
      log.info("Auto-provisioned local account for LDAP user: {}", username);

      String[] displayNameAndMail = lookupLdapDisplayNameAndMail(username);
      Userinfo userinfo = new Userinfo();
      userinfo.setSub(username);
      userinfo.setName(displayNameAndMail[0]);
      userinfo.setEmail(displayNameAndMail[1]);
      userinfoRepository.save(userinfo);

      AccountEvent event =
          new AccountEvent(username, displayNameAndMail[0], displayNameAndMail[1], null, null);
      kafkaTemplate
          .send(accountEventsTopic, event.getSub(), event)
          .whenComplete(
              (result, ex) -> {
                if (ex != null) {
                  log.error("Failed to publish account event for sub {}", username, ex);
                }
              });
    }

    if (authentication.getDetails() instanceof String domain && "GUEST".equals(domain)) {
      Userinfo userinfo = userinfoRepository.getBySub(username);
      if (userinfo == null || userinfo.getName() == null || userinfo.getName().isBlank()) {
        response.sendRedirect("/profile/complete");
        return;
      }
    }

    super.onAuthenticationSuccess(request, response, authentication);
  }

  /** Returns {@code [displayName, mail]}, either element possibly null if not found/unset. */
  private String[] lookupLdapDisplayNameAndMail(String username) {
    try {
      String filter = MessageFormat.format(userSearchFilter, username);
      List<String[]> results = ldapTemplate.search(userSearchBase, filter, ATTRIBUTES_MAPPER);
      return results.isEmpty() ? new String[] {null, null} : results.get(0);
    } catch (Exception ex) {
      log.warn("LDAP attribute lookup failed for user={}: {}", username, ex.getMessage());
      return new String[] {null, null};
    }
  }

  private static final AttributesMapper<String[]> ATTRIBUTES_MAPPER =
      (Attributes attrs) ->
          new String[] {attrValue(attrs, "displayName"), attrValue(attrs, "mail")};

  private static String attrValue(Attributes attrs, String name) throws NamingException {
    Attribute attr = attrs.get(name);
    return attr == null ? null : String.valueOf(attr.get());
  }
}
