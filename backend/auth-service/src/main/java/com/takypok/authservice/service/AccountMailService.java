package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import com.takypok.authservice.model.entity.AccountType;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserinfoRepository;
import java.time.Duration;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * The emails of the account lifecycle: invitation, password reset link, "your account was created".
 * A password is never put in an email: the person sets their own through a one-time link.
 *
 * <p>Methods return false when the message could not be sent (the cause is logged and written to
 * the audit log); the account change that came before it is kept, and an admin can resend.
 */
@Slf4j
@Service
public class AccountMailService {

  private static final int MAX_RESET_REQUESTS_PER_ADDRESS = 3;
  private static final int MAX_RESET_REQUESTS_PER_IP = 20;
  private static final Duration RESET_REQUEST_WINDOW = Duration.ofHours(1);

  private final MailService mail;
  private final AccountTokenService tokens;
  private final UserinfoRepository users;
  private final AuditService audit;
  private final SpringTemplateEngine templates;
  private final StringRedisTemplate redis;
  private final String publicUrl;
  private final String adminAppUrl;
  private final Duration activationTtl;
  private final Duration resetTtl;

  public AccountMailService(
      MailService mail,
      AccountTokenService tokens,
      UserinfoRepository users,
      AuditService audit,
      SpringTemplateEngine templates,
      StringRedisTemplate redis,
      @Value("${auth.public-url}") String publicUrl,
      @Value("${auth.admin-app-url}") String adminAppUrl,
      @Value("${auth.mail.activation-ttl-hours}") long activationHours,
      @Value("${auth.mail.reset-ttl-minutes}") long resetMinutes) {
    this.mail = mail;
    this.tokens = tokens;
    this.users = users;
    this.audit = audit;
    this.templates = templates;
    this.redis = redis;
    this.publicUrl =
        publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
    this.adminAppUrl = adminAppUrl;
    this.activationTtl = Duration.ofHours(activationHours);
    this.resetTtl = Duration.ofMinutes(resetMinutes);
  }

  /** True when an SMTP server is configured. The screens hide what needs mail otherwise. */
  public boolean isEnabled() {
    return mail.isConfigured();
  }

  /** Sends (or re-sends) the invitation to an account that has not been activated yet. */
  public boolean sendInvitation(String sub) {
    Userinfo user = require(sub);
    if (!UserStatus.INVITED.name().equals(user.getStatus())) {
      throw new IllegalArgumentException("This account is not waiting for an invitation.");
    }
    String token = tokens.issue(sub, AccountTokenPurpose.ACTIVATE, activationTtl);
    String link = publicUrl + "/activate?token=" + token;
    long hours = activationTtl.toHours();
    return deliver(
        user,
        null,
        null,
        AuditAction.INVITATION_SENT,
        "You have been invited to the CMS",
        "mail/invitation",
        link,
        hours + " hours",
        "You have been invited to the CMS. Set your password with this link, valid for "
            + hours
            + " hours:\n\n"
            + link
            + "\n\nIf you were not expecting this, ignore this email.");
  }

  /** An admin sends a reset link to an active local account. Nothing changes until it is used. */
  public boolean sendPasswordReset(String sub) {
    Userinfo user = require(sub);
    if (UserStatus.INVITED.name().equals(user.getStatus())) {
      throw new IllegalArgumentException(
          "This account has not been activated: resend the invitation.");
    }
    if (!UserStatus.ACTIVE.name().equals(user.getStatus())) {
      throw new IllegalArgumentException("Reactivate the account before sending a reset link.");
    }
    return sendReset(user, null, null);
  }

  /**
   * "Forgot password": always silent about whether the address exists. Only an active local account
   * with that email gets a message, and a few per hour per address and per network.
   */
  public void requestReset(String email, String ip) {
    if (email == null || email.isBlank() || !mail.isConfigured()) return;
    String address = email.trim().toLowerCase(Locale.ROOT);
    Userinfo user = users.findFirstByEmailIgnoreCase(address).orElse(null);
    if (user == null
        || !AccountType.LOCAL.name().equals(user.getAccountType())
        || !UserStatus.ACTIVE.name().equals(user.getStatus())) {
      return;
    }
    if (overLimit("reset-req:" + address, MAX_RESET_REQUESTS_PER_ADDRESS)
        || overLimit("reset-req-ip:" + ip, MAX_RESET_REQUESTS_PER_IP)) {
      log.info("Password reset request limited for {}", address);
      return;
    }
    sendReset(user, user.getSub(), ip);
  }

  /** "Your account was created" (no password in it): for an account an admin set a password for. */
  public boolean sendCreatedNotice(String sub) {
    Userinfo user = require(sub);
    return deliver(
        user,
        null,
        null,
        null,
        "Your CMS account has been created",
        "mail/created",
        adminAppUrl,
        null,
        "An account has been created for you on the CMS. Your administrator will give you the "
            + "password, or you can choose your own with Forgot password on the sign-in page:\n\n"
            + adminAppUrl
            + "\n\nIf you were not expecting this, ignore this email.");
  }

  private boolean sendReset(Userinfo user, String actor, String ip) {
    String token = tokens.issue(user.getSub(), AccountTokenPurpose.RESET, resetTtl);
    String link = publicUrl + "/reset-password?token=" + token;
    long minutes = resetTtl.toMinutes();
    return deliver(
        user,
        actor,
        ip,
        AuditAction.PASSWORD_RESET_SENT,
        "Reset your CMS password",
        "mail/reset",
        link,
        minutes + " minutes",
        "Choose a new password with this link, valid for "
            + minutes
            + " minutes:\n\n"
            + link
            + "\n\nIf you did not ask for this, ignore this email: your password has not changed.");
  }

  private boolean deliver(
      Userinfo user,
      String actor,
      String ip,
      AuditAction onSuccess,
      String subject,
      String template,
      String link,
      String validFor,
      String text) {
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      throw new IllegalArgumentException("This account has no email address.");
    }
    Context ctx = new Context();
    ctx.setVariable(
        "name", user.getName() != null && !user.getName().isBlank() ? user.getName() : null);
    ctx.setVariable("link", link);
    ctx.setVariable("validFor", validFor);
    try {
      mail.send(user.getEmail(), subject, text, templates.process(template, ctx));
    } catch (MailService.MailDeliveryException ex) {
      record(actor, AuditAction.EMAIL_SEND_FAILED, user.getSub(), subject, ip);
      return false;
    }
    if (onSuccess != null) {
      record(actor, onSuccess, user.getSub(), null, ip);
    }
    return true;
  }

  private void record(String actor, AuditAction action, String target, String detail, String ip) {
    if (actor == null) {
      audit.recordQuietly(action, target, detail);
    } else {
      audit.recordQuietly(actor, action, target, detail, ip);
    }
  }

  private Userinfo require(String sub) {
    return users
        .findById(sub)
        .orElseThrow(() -> new IllegalArgumentException("That account does not exist."));
  }

  /** Counts a request; true once the count passes {@code max} within the window. Fails open. */
  private boolean overLimit(String key, int max) {
    try {
      Long count = redis.opsForValue().increment(key);
      if (count != null && count == 1) {
        redis.expire(key, RESET_REQUEST_WINDOW);
      }
      return count != null && count > max;
    } catch (Exception ex) {
      log.warn("Redis unavailable, reset request not limited: {}", ex.getMessage());
      return false;
    }
  }
}
