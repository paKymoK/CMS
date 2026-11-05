package com.takypok.authservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.config.auth.DomainAuthenticationManager;
import com.takypok.authservice.config.auth.DomainAuthenticationToken;
import com.takypok.authservice.config.auth.MfaPolicy;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserinfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

class DomainAuthenticationManagerTest {

  LdapAuthenticationProvider ldap = mock(LdapAuthenticationProvider.class);
  DaoAuthenticationProvider jdbc = mock(DaoAuthenticationProvider.class);
  JdbcUserDetailsManager users = mock(JdbcUserDetailsManager.class);
  StringRedisTemplate redis = mock(StringRedisTemplate.class);

  @SuppressWarnings("unchecked")
  ValueOperations<String, String> ops = mock(ValueOperations.class);

  UserinfoRepository userinfo = mock(UserinfoRepository.class);
  MfaPolicy mfa = mock(MfaPolicy.class);

  DomainAuthenticationManager manager;
  DomainAuthenticationToken login;
  Authentication verified;

  @BeforeEach
  void setUp() {
    when(redis.opsForValue()).thenReturn(ops);
    manager = new DomainAuthenticationManager(ldap, jdbc, users, redis, userinfo, mfa);
    login = new DomainAuthenticationToken("sam@x.com", "pw", "GUEST", "10.0.0.1");
    verified =
        UsernamePasswordAuthenticationToken.authenticated("sam@x.com", null, java.util.List.of());
    when(jdbc.authenticate(login)).thenReturn(verified);
  }

  private static Userinfo withStatus(String status) {
    Userinfo u = new Userinfo();
    u.setStatus(status);
    return u;
  }

  @Test
  void activeUserSignsIn() {
    when(userinfo.getBySub("sam@x.com")).thenReturn(withStatus("ACTIVE"));

    assertThat(manager.authenticate(login)).isSameAs(verified);
  }

  @Test
  void userWithNoStatusOrNoRowSignsIn() {
    when(userinfo.getBySub("sam@x.com")).thenReturn(null);

    assertThat(manager.authenticate(login)).isSameAs(verified);
  }

  @Test
  void inactiveUserIsRefusedEvenWithTheRightPassword() {
    when(userinfo.getBySub("sam@x.com")).thenReturn(withStatus("INACTIVE"));

    assertThatThrownBy(() -> manager.authenticate(login)).isInstanceOf(DisabledException.class);
    // An inactive account is not a bad password: it must not count toward the lockout.
    verify(ops, never()).increment(any());
  }

  @Test
  void aSecondFactorRequirementFailsClosedBecauseNoStepExistsYet() {
    when(userinfo.getBySub("sam@x.com")).thenReturn(withStatus("ACTIVE"));
    when(mfa.isSecondFactorRequired(verified)).thenReturn(true);

    assertThatThrownBy(() -> manager.authenticate(login))
        .isInstanceOf(AuthenticationServiceException.class);
  }
}
