package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserDirectoryRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.authservice.service.impl.UserServiceImpl;
import com.takypok.core.exception.ApplicationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock JdbcUserDetailsManager userDetailsManager;
  @Mock UserinfoRepository userinfoRepository;
  @Mock UserDirectoryRepository userDirectory;
  @Mock SuperAdminService superAdminService;

  @InjectMocks UserServiceImpl service;

  Userinfo john;

  @BeforeEach
  void setUp() {
    john = new Userinfo();
    john.setSub("john");
  }

  /** Makes the guard run the change it is given, as the real one does. */
  private void guardRunsChange() {
    doAnswer(
            inv -> {
              ((Runnable) inv.getArgument(0)).run();
              return null;
            })
        .when(superAdminService)
        .guardLastSuperAdmin(any());
  }

  @Test
  void deactivatingSavesInactiveAndRevokesTokens() {
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));
    guardRunsChange();

    service.setStatus("john", UserStatus.INACTIVE);

    assertThat(john.getStatus()).isEqualTo("INACTIVE");
    verify(userinfoRepository).saveAndFlush(john);
    verify(userDirectory).revokeAuthorizations("john");
  }

  @Test
  void deactivatingTheLastSuperAdminIsRefusedAndTokensKept() {
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));
    doThrow(new ApplicationException(com.takypok.core.model.Message.Application.ERROR, "last"))
        .when(superAdminService)
        .guardLastSuperAdmin(any());

    assertThatThrownBy(() -> service.setStatus("john", UserStatus.INACTIVE))
        .isInstanceOf(ApplicationException.class);

    verify(userDirectory, never()).revokeAuthorizations(any());
  }

  @Test
  void reactivatingNeedsNoGuardAndRevokesNothing() {
    john.setStatus("INACTIVE");
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));

    service.setStatus("john", UserStatus.ACTIVE);

    assertThat(john.getStatus()).isEqualTo("ACTIVE");
    verify(userinfoRepository).saveAndFlush(john);
    verify(superAdminService, never()).guardLastSuperAdmin(any());
    verify(userDirectory, never()).revokeAuthorizations(any());
  }

  @Test
  void unknownUserIsRejected() {
    when(userinfoRepository.findById("ghost")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.setStatus("ghost", UserStatus.INACTIVE))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("User not found");
  }

  @Test
  void searchClampsPageSizeAndNormalisesStatus() {
    when(userDirectory.search(any(), any(), any(), any(), eq(0), eq(100)))
        .thenReturn(new UserDirectoryRepository.Result(List.of(), 0));
    when(userDirectory.rolesFor(any())).thenReturn(java.util.Map.of());

    var page = service.search("  ", "inactive", " ", null, -3, 5000);

    verify(userDirectory).search(eq("  "), eq("INACTIVE"), eq(null), eq(null), eq(0), eq(100));
    assertThat(page.getSize()).isEqualTo(100);
    assertThat(page.getTotalPages()).isZero();
  }

  @Test
  void searchRejectsUnknownStatus() {
    assertThatThrownBy(() -> service.search(null, "banana", null, null, 0, 20))
        .isInstanceOf(ApplicationException.class);
  }

  private void inactive(String type) {
    john.setStatus("INACTIVE");
    john.setAccountType(type);
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));
  }

  @Test
  void deletingAnInactiveLocalAccountPurgesItAndReservesTheSub() {
    inactive("LOCAL");
    guardRunsChange();

    service.delete("john", "admin");

    verify(userDirectory).purge("john", "admin");
  }

  @Test
  void deletingRefusesActiveDirectoryAndOwnAccounts() {
    john.setAccountType("LOCAL");
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));
    assertThatThrownBy(() -> service.delete("john", "admin"))
        .isInstanceOf(ApplicationException.class); // still active

    john.setStatus("INACTIVE");
    john.setAccountType("DIRECTORY");
    assertThatThrownBy(() -> service.delete("john", "admin"))
        .isInstanceOf(ApplicationException.class);

    assertThatThrownBy(() -> service.delete("john", "john"))
        .isInstanceOf(ApplicationException.class);
    verify(userDirectory, never()).purge(any(), any());
  }

  @Test
  void deletingStopsWhenTheGuardRefuses() {
    inactive("LOCAL");
    doThrow(new ApplicationException(com.takypok.core.model.Message.Application.ERROR, "last"))
        .when(superAdminService)
        .guardLastSuperAdmin(any());

    assertThatThrownBy(() -> service.delete("john", "admin"))
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void creatingARetiredUsernameIsRefused() {
    org.springframework.test.util.ReflectionTestUtils.setField(
        service, "internalDomain", "corp.local");
    when(userDirectory.isTombstoned("old@x.com")).thenReturn(true);

    assertThatThrownBy(
            () ->
                service.create(
                    new com.takypok.authservice.model.request.CreateUserRequest(
                        "old@x.com", "password1")))
        .isInstanceOf(ApplicationException.class);
    verify(userDetailsManager, never()).createUser(any());
  }

  @Test
  void directoryAccountRefusesNameAndEmailButTakesDisplayName() {
    john.setAccountType("DIRECTORY");
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));
    var bad = new com.takypok.authservice.model.request.UpdateUserProfileRequest();
    bad.setName("Other");
    assertThatThrownBy(() -> service.updateProfile("john", bad))
        .isInstanceOf(ApplicationException.class);

    var ok = new com.takypok.authservice.model.request.UpdateUserProfileRequest();
    ok.setDisplayName("Johnny");
    ok.setDateOfBirth(java.time.LocalDate.of(1990, 1, 2));
    service.updateProfile("john", ok);

    assertThat(john.getDisplayName()).isEqualTo("Johnny");
    assertThat(john.getDateOfBirth()).isEqualTo(java.time.LocalDate.of(1990, 1, 2));
  }

  private static com.takypok.authservice.model.request.UpdateUserProfileRequest json(String body)
      throws Exception {
    return new com.fasterxml.jackson.databind.ObjectMapper()
        .findAndRegisterModules()
        .readValue(body, com.takypok.authservice.model.request.UpdateUserProfileRequest.class);
  }

  @Test
  void profileFieldsSentAsNullAreClearedAndOthersKept() throws Exception {
    john.setName("John");
    john.setEmail("j@x.com");
    john.setDisplayName("Johnny");
    john.setDepartmentId(1L);
    john.setUnitId(2L);
    john.setDateOfBirth(java.time.LocalDate.of(1990, 1, 2));
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));

    service.updateProfile(
        "john", json("{\"displayName\":null,\"dateOfBirth\":null,\"unitId\":null}"));

    assertThat(john.getDisplayName()).isNull();
    assertThat(john.getDateOfBirth()).isNull();
    assertThat(john.getUnitId()).isNull();
    assertThat(john.getName()).isEqualTo("John"); // not sent: kept
    assertThat(john.getEmail()).isEqualTo("j@x.com");
    assertThat(john.getDepartmentId()).isEqualTo(1L);
  }

  @Test
  void blankTextClearsTheField() throws Exception {
    john.setName("John");
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));

    service.updateProfile("john", json("{\"name\":\"  \"}"));

    assertThat(john.getName()).isNull();
  }

  @Test
  void emailCannotBeCleared() throws Exception {
    john.setEmail("j@x.com");
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));

    assertThatThrownBy(() -> service.updateProfile("john", json("{\"email\":null}")))
        .isInstanceOf(ApplicationException.class);
    assertThat(john.getEmail()).isEqualTo("j@x.com");
  }

  @Test
  void unitWithoutDepartmentIsRefused() throws Exception {
    john.setDepartmentId(1L);
    john.setUnitId(2L);
    when(userinfoRepository.findById("john")).thenReturn(Optional.of(john));

    assertThatThrownBy(() -> service.updateProfile("john", json("{\"departmentId\":null}")))
        .isInstanceOf(ApplicationException.class);
    verify(userinfoRepository, never()).save(any());
  }

  @Test
  void getAttachesRoles() {
    when(userDirectory.find("john"))
        .thenReturn(
            Optional.of(
                new UserDirectoryRepository.Row(
                    "john", "John", "j@x.com", null, null, "ACTIVE", "LOCAL", null, null, null,
                    null)));
    when(userDirectory.rolesFor(List.of("john")))
        .thenReturn(
            java.util.Map.of(
                "john", List.of(new UserDirectoryRepository.RoleRow("john", "en", "ADMIN"))));

    var user = service.get("john");

    assertThat(user.getRoles()).hasSize(1);
    assertThat(user.getRoles().get(0).getProjectId()).isEqualTo("en");
  }
}
