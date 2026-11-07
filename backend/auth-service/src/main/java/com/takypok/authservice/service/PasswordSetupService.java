package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import com.takypok.authservice.model.entity.AccountType;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserDirectoryRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Uses a link from an email to set a password: activating an invited account, or resetting a
 * forgotten password. The password is checked first, so a typo does not burn the link; the link is
 * then used up in the same transaction as the change, and every refresh token of the account is
 * dropped (a reset must end a session someone else may hold).
 */
@Service
@RequiredArgsConstructor
public class PasswordSetupService {

  /** The link is unknown, used, expired, or the account is no longer in a state it applies to. */
  public static class InvalidLinkException extends RuntimeException {
    public InvalidLinkException() {
      super("This link is not valid any more.");
    }
  }

  private final AccountTokenService tokens;
  private final UserinfoRepository users;
  private final UserDirectoryRepository directory;
  private final AuditService audit;
  private final SessionService sessions;

  /** The invited account behind an activation link, to greet by name. Does not use the link up. */
  public Optional<Userinfo> peekActivation(String rawToken) {
    return tokens
        .peek(rawToken, AccountTokenPurpose.ACTIVATE)
        .flatMap(users::findById)
        .filter(u -> UserStatus.INVITED.name().equals(u.getStatus()));
  }

  public Optional<Userinfo> peekReset(String rawToken) {
    return tokens
        .peek(rawToken, AccountTokenPurpose.RESET)
        .flatMap(users::findById)
        .filter(PasswordSetupService::canReset);
  }

  @Transactional
  public void activate(String rawToken, String newPassword, String ip) {
    PasswordPolicy.require(newPassword);
    String sub =
        tokens
            .consume(rawToken, AccountTokenPurpose.ACTIVATE)
            .orElseThrow(InvalidLinkException::new);
    Userinfo user = users.findById(sub).orElseThrow(InvalidLinkException::new);
    if (!UserStatus.INVITED.name().equals(user.getStatus())) {
      throw new InvalidLinkException(); // cancelled (deactivated) since the email was sent
    }
    directory.setPassword(sub, encode(newPassword));
    user.setStatus(UserStatus.ACTIVE.name());
    users.save(user);
    sessions.endAll(sub, null, "account activated");
    audit.recordAs(sub, AuditAction.ACCOUNT_ACTIVATED, sub, null, ip);
  }

  @Transactional
  public void reset(String rawToken, String newPassword, String ip) {
    PasswordPolicy.require(newPassword);
    String sub =
        tokens.consume(rawToken, AccountTokenPurpose.RESET).orElseThrow(InvalidLinkException::new);
    Userinfo user = users.findById(sub).orElseThrow(InvalidLinkException::new);
    if (!canReset(user)) {
      throw new InvalidLinkException();
    }
    directory.setPassword(sub, encode(newPassword));
    sessions.endAll(sub, null, "password reset");
    audit.recordAs(sub, AuditAction.PASSWORD_RESET_DONE, sub, null, ip);
  }

  private static boolean canReset(Userinfo u) {
    return AccountType.LOCAL.name().equals(u.getAccountType())
        && UserStatus.ACTIVE.name().equals(u.getStatus());
  }

  private static String encode(String raw) {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(raw);
  }
}
