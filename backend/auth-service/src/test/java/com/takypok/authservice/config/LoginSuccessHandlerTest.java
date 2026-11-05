package com.takypok.authservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.config.auth.LdapAutoProvisionSuccessHandler;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.authservice.service.AuditService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

class LoginSuccessHandlerTest {

  JdbcUserDetailsManager users = mock(JdbcUserDetailsManager.class);
  UserinfoRepository repo = mock(UserinfoRepository.class);
  LdapTemplate ldap = mock(LdapTemplate.class);
  AuditService audit = mock(AuditService.class);
  LdapAutoProvisionSuccessHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new LdapAutoProvisionSuccessHandler(
            users, mock(PasswordEncoder.class), repo, ldap, "ou=people", "(uid={0})", audit);
    when(users.userExists(anyString())).thenReturn(true);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static List one(String[] row) {
    List<String[]> rows = new java.util.ArrayList<>();
    rows.add(row);
    return rows;
  }

  private void signIn(String sub) throws Exception {
    handler.onAuthenticationSuccess(
        new MockHttpServletRequest(),
        new MockHttpServletResponse(),
        new UsernamePasswordAuthenticationToken(sub, "x"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void staffLoginStampsLastLoginAndRefreshesNameAndEmail() throws Exception {
    Userinfo staff = new Userinfo();
    staff.setSub("lan");
    staff.setAccountType("DIRECTORY");
    staff.setName("Old");
    staff.setEmail("old@corp");
    when(repo.getBySub("lan")).thenReturn(staff);
    when(ldap.search(anyString(), anyString(), any(AttributesMapper.class)))
        .thenReturn(one(new String[] {"Lan Nguyen", "lan@corp"}));

    signIn("lan");

    assertThat(staff.getName()).isEqualTo("Lan Nguyen");
    assertThat(staff.getEmail()).isEqualTo("lan@corp");
    assertThat(staff.getLastLoginAt()).isNotNull();
    verify(repo).save(staff);
  }

  @Test
  @SuppressWarnings("unchecked")
  void directoryReturningNothingKeepsStoredValues() throws Exception {
    Userinfo staff = new Userinfo();
    staff.setSub("lan");
    staff.setAccountType("DIRECTORY");
    staff.setName("Old");
    when(repo.getBySub("lan")).thenReturn(staff);
    when(ldap.search(anyString(), anyString(), any(AttributesMapper.class)))
        .thenReturn(one(new String[] {null, null}));

    signIn("lan");

    assertThat(staff.getName()).isEqualTo("Old");
    assertThat(staff.getLastLoginAt()).isNotNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  void localLoginStampsLastLoginWithoutTouchingLdap() throws Exception {
    Userinfo guest = new Userinfo();
    guest.setSub("g@x.com");
    guest.setName("Guest");
    when(repo.getBySub("g@x.com")).thenReturn(guest);

    signIn("g@x.com");

    assertThat(guest.getLastLoginAt()).isNotNull();
    verify(audit).recordQuietly("g@x.com", AuditAction.LOGIN_SUCCESS, "g@x.com", null, "127.0.0.1");
    verify(ldap, never()).search(anyString(), anyString(), any(AttributesMapper.class));
  }
}
