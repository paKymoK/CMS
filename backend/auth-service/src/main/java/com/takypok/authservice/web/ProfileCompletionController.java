package com.takypok.authservice.web;

import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.model.event.AccountEvent;
import com.takypok.authservice.repository.UserinfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Guest-only "complete your profile" gate (Phase 7) — LDAP users never see this, their name is
 * captured automatically from the directory at first login (see {@link
 * com.takypok.authservice.config.auth.LdapAutoProvisionSuccessHandler}). A guest has no directory
 * to source a name from, so they're asked for it once, here, the first time they actually log in.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileCompletionController {

  private final UserinfoRepository userinfoRepository;
  private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
  private final RequestCache requestCache;

  @Value("${account-events.topic}")
  private String accountEventsTopic;

  @GetMapping("/profile/complete")
  public String complete(@RequestParam(required = false) String error, Model model) {
    if (error != null) {
      model.addAttribute("error", true);
    }
    return "profile-complete";
  }

  @PostMapping("/profile/complete")
  public void submit(
      @RequestParam String name,
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    if (name == null || name.isBlank()) {
      response.sendRedirect("/profile/complete?error=true");
      return;
    }

    String sub = authentication.getName();
    Userinfo userinfo = userinfoRepository.getBySub(sub);
    if (userinfo == null) {
      userinfo = new Userinfo();
      userinfo.setSub(sub);
    }
    userinfo.setName(name.trim());
    userinfoRepository.save(userinfo);

    AccountEvent event =
        new AccountEvent(
            sub,
            userinfo.getName(),
            userinfo.getEmail(),
            userinfo.getDepartmentId(),
            userinfo.getUnitId());
    kafkaTemplate
        .send(accountEventsTopic, sub, event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish account event for sub {}", sub, ex);
              }
            });

    // The original /oauth2/authorize?... request was never consumed — it's still sitting in the
    // session's RequestCache, parked there by LdapAutoProvisionSuccessHandler's redirect here
    // instead of its usual super.onAuthenticationSuccess() call. Resume it now.
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    response.sendRedirect(savedRequest != null ? savedRequest.getRedirectUrl() : "/");
  }
}
