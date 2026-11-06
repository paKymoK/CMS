package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserDirectoryRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PasswordSetupServiceTest {

  AccountTokenService tokens = mock(AccountTokenService.class);
  UserinfoRepository users = mock(UserinfoRepository.class);
  UserDirectoryRepository directory = mock(UserDirectoryRepository.class);
  AuditService audit = mock(AuditService.class);
  PasswordSetupService service = new PasswordSetupService(tokens, users, directory, audit);

  Userinfo ann;

  @BeforeEach
  void setUp() {
    ann = new Userinfo();
    ann.setSub("ann");
    ann.setAccountType("LOCAL");
    ann.setStatus("INVITED");
    when(users.findById("ann")).thenReturn(Optional.of(ann));
  }

  @Test
  void activatingSetsAnEncodedPasswordMakesTheAccountActiveAndDropsTokens() {
    when(tokens.consume("tok", AccountTokenPurpose.ACTIVATE)).thenReturn(Optional.of("ann"));

    service.activate("tok", "a-good-password", "10.0.0.1");

    var stored = ArgumentCaptor.forClass(String.class);
    verify(directory).setPassword(org.mockito.ArgumentMatchers.eq("ann"), stored.capture());
    assertThat(stored.getValue()).startsWith("{bcrypt}").doesNotContain("a-good-password");
    assertThat(ann.getStatus()).isEqualTo("ACTIVE");
    verify(users).save(ann);
    verify(directory).revokeAuthorizations("ann");
    verify(audit).recordAs("ann", AuditAction.ACCOUNT_ACTIVATED, "ann", null, "10.0.0.1");
  }

  @Test
  void aWeakPasswordDoesNotUseTheLinkUp() {
    assertThatThrownBy(() -> service.activate("tok", "short", "ip"))
        .isInstanceOf(IllegalArgumentException.class);
    verify(tokens, never()).consume(anyString(), any());
    verify(directory, never()).setPassword(any(), any());
  }

  @Test
  void anUnknownUsedOrExpiredLinkIsRefused() {
    when(tokens.consume("bad", AccountTokenPurpose.ACTIVATE)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.activate("bad", "a-good-password", "ip"))
        .isInstanceOf(PasswordSetupService.InvalidLinkException.class);
    verify(directory, never()).setPassword(any(), any());
  }

  @Test
  void anInvitationCancelledByDeactivationNoLongerWorks() {
    ann.setStatus("INACTIVE");
    when(tokens.consume("tok", AccountTokenPurpose.ACTIVATE)).thenReturn(Optional.of("ann"));

    assertThatThrownBy(() -> service.activate("tok", "a-good-password", "ip"))
        .isInstanceOf(PasswordSetupService.InvalidLinkException.class);
    verify(directory, never()).setPassword(any(), any());
  }

  @Test
  void resettingChangesThePasswordAndDropsTokens() {
    ann.setStatus("ACTIVE");
    when(tokens.consume("tok", AccountTokenPurpose.RESET)).thenReturn(Optional.of("ann"));

    service.reset("tok", "another-password", "10.0.0.2");

    verify(directory).setPassword(org.mockito.ArgumentMatchers.eq("ann"), anyString());
    verify(directory).revokeAuthorizations("ann");
    verify(audit).recordAs("ann", AuditAction.PASSWORD_RESET_DONE, "ann", null, "10.0.0.2");
  }

  @Test
  void aResetLinkIsRefusedForAnInactiveOrDirectoryAccount() {
    when(tokens.consume("tok", AccountTokenPurpose.RESET)).thenReturn(Optional.of("ann"));
    ann.setStatus("INACTIVE");
    assertThatThrownBy(() -> service.reset("tok", "another-password", "ip"))
        .isInstanceOf(PasswordSetupService.InvalidLinkException.class);

    ann.setStatus("ACTIVE");
    ann.setAccountType("DIRECTORY");
    assertThatThrownBy(() -> service.reset("tok", "another-password", "ip"))
        .isInstanceOf(PasswordSetupService.InvalidLinkException.class);
    verify(directory, never()).setPassword(any(), any());
  }

  @Test
  void peekOnlyShowsTheFormForAnAccountTheLinkStillApplies() {
    when(tokens.peek("tok", AccountTokenPurpose.ACTIVATE)).thenReturn(Optional.of("ann"));
    assertThat(service.peekActivation("tok")).contains(ann);

    ann.setStatus("ACTIVE"); // already activated
    assertThat(service.peekActivation("tok")).isEmpty();
  }
}
