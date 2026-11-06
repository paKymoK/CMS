package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserinfoRepository;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class AccountMailServiceTest {

  MailService mail = mock(MailService.class);
  AccountTokenService tokens = mock(AccountTokenService.class);
  UserinfoRepository users = mock(UserinfoRepository.class);
  AuditService audit = mock(AuditService.class);
  StringRedisTemplate redis = mock(StringRedisTemplate.class);

  @SuppressWarnings("unchecked")
  ValueOperations<String, String> ops = mock(ValueOperations.class);

  AccountMailService service;
  Userinfo ann;

  @BeforeEach
  void setUp() {
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    resolver.setCharacterEncoding("UTF-8");
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(resolver);
    when(redis.opsForValue()).thenReturn(ops);
    when(mail.isConfigured()).thenReturn(true);
    service =
        new AccountMailService(
            mail,
            tokens,
            users,
            audit,
            engine,
            redis,
            "http://auth.test/",
            "http://cms.test",
            48,
            60);

    ann = new Userinfo();
    ann.setSub("ann@guest.test");
    ann.setEmail("ann@guest.test");
    ann.setName("Ann <b>Lee</b>");
    ann.setAccountType("LOCAL");
    ann.setStatus("INVITED");
    when(users.findById("ann@guest.test")).thenReturn(Optional.of(ann));
    when(users.findFirstByEmailIgnoreCase("ann@guest.test")).thenReturn(Optional.of(ann));
    when(tokens.issue(eq("ann@guest.test"), any(), any())).thenReturn("RAWTOKEN");
  }

  private String sentHtml() {
    var html = ArgumentCaptor.forClass(String.class);
    verify(mail).send(eq("ann@guest.test"), anyString(), anyString(), html.capture());
    return html.getValue();
  }

  @Test
  void theInvitationCarriesAOneTimeLinkAndEscapesTheName() {
    assertThat(service.sendInvitation("ann@guest.test")).isTrue();

    var text = ArgumentCaptor.forClass(String.class);
    var html = ArgumentCaptor.forClass(String.class);
    verify(mail)
        .send(
            eq("ann@guest.test"),
            eq("You have been invited to the CMS"),
            text.capture(),
            html.capture());
    assertThat(text.getValue())
        .contains("http://auth.test/activate?token=RAWTOKEN")
        .contains("48 hours");
    assertThat(html.getValue()).contains("http://auth.test/activate?token=RAWTOKEN");
    assertThat(html.getValue()).contains("Ann &lt;b&gt;Lee&lt;/b&gt;").doesNotContain("<b>Lee</b>");
    verify(tokens).issue("ann@guest.test", AccountTokenPurpose.ACTIVATE, Duration.ofHours(48));
    verify(audit).recordQuietly(AuditAction.INVITATION_SENT, "ann@guest.test", null);
  }

  @Test
  void aFailedSendReturnsFalseAndIsAudited() {
    doThrow(new MailService.MailDeliveryException("no", null))
        .when(mail)
        .send(anyString(), anyString(), anyString(), anyString());

    assertThat(service.sendInvitation("ann@guest.test")).isFalse();
    verify(audit)
        .recordQuietly(eq(AuditAction.EMAIL_SEND_FAILED), eq("ann@guest.test"), anyString());
    verify(audit, never()).recordQuietly(AuditAction.INVITATION_SENT, "ann@guest.test", null);
  }

  @Test
  void aPasswordIsNeverPutInAnEmail() {
    service.sendCreatedNotice("ann@guest.test");

    var text = ArgumentCaptor.forClass(String.class);
    verify(mail).send(anyString(), anyString(), text.capture(), anyString());
    assertThat(text.getValue()).contains("http://cms.test").doesNotContainIgnoringCase("password:");
  }

  @Test
  void anAdminResetNeedsAnActiveAccount() {
    assertThatThrownBy(() -> service.sendPasswordReset("ann@guest.test")) // still invited
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("invitation");
    ann.setStatus("INACTIVE");
    assertThatThrownBy(() -> service.sendPasswordReset("ann@guest.test"))
        .isInstanceOf(IllegalArgumentException.class);

    ann.setStatus("ACTIVE");
    assertThat(service.sendPasswordReset("ann@guest.test")).isTrue();
    verify(tokens).issue("ann@guest.test", AccountTokenPurpose.RESET, Duration.ofMinutes(60));
  }

  @Test
  void forgotPasswordSendsOnlyForAnActiveLocalAccountAndSaysNothingElse() {
    when(users.findFirstByEmailIgnoreCase("nobody@guest.test")).thenReturn(Optional.empty());
    service.requestReset("nobody@guest.test", "10.0.0.1");
    ann.setStatus("INACTIVE");
    service.requestReset("ann@guest.test", "10.0.0.1");
    ann.setStatus("ACTIVE");
    ann.setAccountType("DIRECTORY");
    service.requestReset("ann@guest.test", "10.0.0.1");
    service.requestReset("", "10.0.0.1");
    verify(mail, never()).send(anyString(), anyString(), anyString(), anyString());

    ann.setAccountType("LOCAL");
    when(ops.increment(anyString())).thenReturn(1L);
    service.requestReset("  Ann@Guest.Test ", "10.0.0.1");
    sentHtml();
    verify(audit)
        .recordQuietly(
            "ann@guest.test", AuditAction.PASSWORD_RESET_SENT, "ann@guest.test", null, "10.0.0.1");
  }

  @Test
  void forgotPasswordIsRateLimitedPerAddress() {
    ann.setStatus("ACTIVE");
    when(ops.increment("reset-req:ann@guest.test")).thenReturn(4L); // the fourth within the hour
    when(ops.increment("reset-req-ip:10.0.0.1")).thenReturn(1L);

    service.requestReset("ann@guest.test", "10.0.0.1");

    verify(mail, never()).send(anyString(), anyString(), anyString(), anyString());
  }

  @Test
  void anAccountWithNoEmailCannotBeMailed() {
    ann.setEmail(null);
    assertThatThrownBy(() -> service.sendInvitation("ann@guest.test"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
