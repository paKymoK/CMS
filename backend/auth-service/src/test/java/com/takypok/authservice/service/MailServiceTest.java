package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

class MailServiceTest {

  @SuppressWarnings("unchecked")
  private static ObjectProvider<JavaMailSender> provider(JavaMailSender sender) {
    ObjectProvider<JavaMailSender> p = mock(ObjectProvider.class);
    when(p.getIfAvailable()).thenReturn(sender);
    return p;
  }

  @Test
  void anEmptyTrustSettingIsNeverPassedOnBecauseThatWouldRejectEveryServer() {
    JavaMailSenderImpl impl = new JavaMailSenderImpl();

    new MailService(provider(impl), "smtp.example.org", "a@b.c", "");
    new MailService(provider(impl), "smtp.example.org", "a@b.c", "   ");

    assertThat(impl.getJavaMailProperties()).doesNotContainKey("mail.smtp.ssl.trust");
  }

  @Test
  void aGivenTrustHostIsApplied() {
    JavaMailSenderImpl impl = new JavaMailSenderImpl();

    new MailService(provider(impl), "localhost", "a@b.c", " localhost ");

    assertThat(impl.getJavaMailProperties()).containsEntry("mail.smtp.ssl.trust", "localhost");
  }

  @Test
  void mailIsOffWithoutAHostAndSendingThenFailsCleanly() {
    MailService off = new MailService(provider(new JavaMailSenderImpl()), "", "a@b.c", "");

    assertThat(off.isConfigured()).isFalse();
    assertThatThrownBy(() -> off.send("x@y.z", "s", "t", "<p>h</p>"))
        .isInstanceOf(MailService.MailDeliveryException.class);
  }
}
