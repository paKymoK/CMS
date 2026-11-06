package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailDomainPolicyTest {

  EmailDomainPolicy policy = new EmailDomainPolicy("example.com");

  @Test
  void theCompanyDomainIsInternalWhateverTheCase() {
    assertThat(policy.isInternal("lan@example.com")).isTrue();
    assertThat(policy.isInternal("LAN@Example.COM")).isTrue();
    assertThat(policy.isInternal("  lan@example.com ")).isTrue();
  }

  @Test
  void subdomainsOfTheCompanyDomainAreInternalToo() {
    assertThat(policy.isInternal("lan@mail.example.com")).isTrue();
    assertThat(policy.isInternal("lan@a.b.example.com")).isTrue();
  }

  @Test
  void lookAlikeDomainsAreNotInternal() {
    assertThat(policy.isInternal("x@notexample.com")).isFalse();
    assertThat(policy.isInternal("x@example.com.evil.org")).isFalse();
    assertThat(policy.isInternal("x@example.org")).isFalse();
    assertThat(policy.isInternal("x@gmail.com")).isFalse();
  }

  @Test
  void severalDomainsAreAcceptedWithOrWithoutAnAtSign() {
    EmailDomainPolicy many = new EmailDomainPolicy(" a.com , @b.vn ,, ");
    assertThat(many.isInternal("x@a.com")).isTrue();
    assertThat(many.isInternal("x@b.vn")).isTrue();
    assertThat(many.isInternal("x@c.com")).isFalse();
  }

  @Test
  void nothingIsBlockedWhenNoDomainIsConfigured() {
    assertThat(new EmailDomainPolicy("").isInternal("x@example.com")).isFalse();
    assertThat(new EmailDomainPolicy(null).isInternal("x@example.com")).isFalse();
  }

  @Test
  void aGuestAddressMustBeAnAddressOutsideTheCompany() {
    assertThatCode(() -> policy.requireGuestAddress("pat@partner.org", "Email"))
        .doesNotThrowAnyException();
    assertThatThrownBy(() -> policy.requireGuestAddress("pat@example.com", "Email"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("@example.com");
    for (String bad : new String[] {null, "", "plain", "@x.com", "a@", "a@b@c.com"}) {
      assertThatThrownBy(() -> policy.requireGuestAddress(bad, "Username"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Username must be an email address");
    }
  }
}
