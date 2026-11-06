package com.takypok.authservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Sends one email through the SMTP server in {@code spring.mail.*}. With no host configured, mail
 * is off: {@link #isConfigured()} is false and the screens hide what needs it.
 */
@Slf4j
@Service
public class MailService {

  /** Thrown when a message could not be handed to the mail server. */
  public static class MailDeliveryException extends RuntimeException {
    public MailDeliveryException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private final ObjectProvider<JavaMailSender> sender;
  private final String host;
  private final String from;

  public MailService(
      ObjectProvider<JavaMailSender> sender,
      @Value("${spring.mail.host:}") String host,
      @Value("${auth.mail.from}") String from,
      @Value("${auth.mail.ssl-trust:}") String sslTrust) {
    this.sender = sender;
    this.host = host == null ? "" : host.trim();
    this.from = from;
    // Set only when given: an empty trust list would make every server untrusted.
    if (sslTrust != null
        && !sslTrust.isBlank()
        && sender.getIfAvailable() instanceof JavaMailSenderImpl impl) {
      impl.getJavaMailProperties().setProperty("mail.smtp.ssl.trust", sslTrust.trim());
    }
  }

  public boolean isConfigured() {
    return !host.isBlank() && sender.getIfAvailable() != null;
  }

  /** Sends a text and HTML version of one message to one recipient. */
  public void send(String to, String subject, String text, String html) {
    JavaMailSender mailSender = sender.getIfAvailable();
    if (host.isBlank() || mailSender == null) {
      throw new MailDeliveryException("Mail is not configured", null);
    }
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text, html);
      mailSender.send(message);
    } catch (Exception ex) {
      // The top-level message is often just "Could not convert socket to TLS": the root cause says
      // why
      // (untrusted certificate, wrong host name, refused login), which is what an admin needs.
      log.warn(
          "Sending mail to {} failed: {} (cause: {})",
          to,
          ex.getMessage(),
          NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
      throw new MailDeliveryException("The email could not be sent", ex);
    }
  }
}
