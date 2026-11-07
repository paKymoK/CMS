package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.AccountType;
import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.authservice.repository.UserDirectoryRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.authservice.service.AuditService;
import com.takypok.authservice.service.EmailDomainPolicy;
import com.takypok.authservice.service.PasswordPolicy;
import com.takypok.authservice.service.SessionService;
import com.takypok.authservice.service.SuperAdminService;
import com.takypok.authservice.service.UserService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.PageResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final JdbcUserDetailsManager userDetailsManager;
  private final UserinfoRepository userinfoRepository;
  private final UserDirectoryRepository userDirectory;
  private final SuperAdminService superAdminService;
  private final AuditService auditService;
  private final EmailDomainPolicy emailDomainPolicy;
  private final SessionService sessionService;

  private static final int MAX_PAGE_SIZE = 100;

  @Override
  @Transactional
  public void create(CreateUserRequest request) {
    requireNewLocalAccount(request.getUsername());
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    UserDetails user =
        User.builder()
            .username(request.getUsername())
            .password(encoder.encode(request.getPassword()))
            .roles("USER")
            .build();
    userDetailsManager.createUser(user);

    // `sub` IS the email for guest/local accounts (enforced by validateUsername below) — `name`
    // has no source yet and stays null until the guest completes their profile on first login
    // (see LdapAutoProvisionSuccessHandler's GUEST branch / ProfileCompletionController).
    Userinfo userinfo = new Userinfo();
    userinfo.setSub(request.getUsername());
    userinfo.setEmail(request.getUsername());
    userinfo.setAccountType(AccountType.LOCAL.name());
    userinfoRepository.save(userinfo);
    auditService.record(AuditAction.USER_CREATED, request.getUsername(), "local account");
  }

  @Override
  @Transactional
  public void createInvited(String username, String name) {
    requireNewLocalAccount(username);
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // Nobody knows this password, so the account cannot sign in until the person sets their own.
    String unusable =
        encoder.encode(java.util.UUID.randomUUID() + java.util.UUID.randomUUID().toString());
    userDetailsManager.createUser(
        User.builder().username(username).password(unusable).roles("USER").build());
    Userinfo userinfo = new Userinfo();
    userinfo.setSub(username);
    userinfo.setEmail(username);
    userinfo.setName(blankToNull(name));
    userinfo.setAccountType(AccountType.LOCAL.name());
    userinfo.setStatus(UserStatus.INVITED.name());
    userinfoRepository.save(userinfo);
    auditService.record(AuditAction.USER_CREATED, username, "invited by email");
  }

  @Override
  @Transactional
  public void updateProfile(String sub, UpdateUserProfileRequest request) {
    Userinfo userinfo =
        userinfoRepository
            .findById(sub)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "User not found"));
    boolean directory = AccountType.DIRECTORY.name().equals(userinfo.getAccountType());
    if (directory && (request.isSent("name") || request.isSent("email"))) {
      throw new ApplicationException(
          Message.Application.ERROR, "Name and email of a directory account come from LDAP");
    }
    if (request.isSent("name")) {
      userinfo.setName(blankToNull(request.getName()));
    }
    if (request.isSent("email")) {
      String email = blankToNull(request.getEmail());
      if (email == null) {
        throw new ApplicationException(Message.Application.ERROR, "Email cannot be cleared");
      }
      // Only LOCAL accounts get here (a directory account's email is refused above).
      emailDomainPolicy.requireGuestAddress(email, "Email");
      requireFreeEmail(email, sub);
      userinfo.setEmail(email);
    }
    if (request.isSent("departmentId")) {
      userinfo.setDepartmentId(request.getDepartmentId());
    }
    if (request.isSent("unitId")) {
      userinfo.setUnitId(request.getUnitId());
    }
    if (userinfo.getUnitId() != null && userinfo.getDepartmentId() == null) {
      throw new ApplicationException(
          Message.Application.ERROR, "A unit needs a department; clear the unit too");
    }
    if (request.isSent("displayName")) {
      userinfo.setDisplayName(blankToNull(request.getDisplayName()));
    }
    if (request.isSent("dateOfBirth")) {
      userinfo.setDateOfBirth(request.getDateOfBirth());
    }
    userinfoRepository.save(userinfo);
    auditService.record(
        AuditAction.PROFILE_UPDATED, sub, "fields: " + String.join(", ", request.sentFields()));
  }

  @Override
  public PageResponse<UserResponse> search(
      String q, String status, String role, String projectId, int page, int size) {
    int safePage = Math.max(page, 0);
    int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    UserDirectoryRepository.Result result =
        userDirectory.search(
            q, parseStatus(status), blankToNull(role), blankToNull(projectId), safePage, safeSize);
    Map<String, List<UserDirectoryRepository.RoleRow>> roles =
        userDirectory.rolesFor(
            result.rows().stream().map(UserDirectoryRepository.Row::sub).toList());
    return PageResponse.<UserResponse>builder()
        .content(result.rows().stream().map(r -> toResponse(r, roles.get(r.sub()))).toList())
        .page(safePage)
        .size(safeSize)
        .totalElements(result.total())
        .totalPages((result.total() + safeSize - 1) / safeSize)
        .build();
  }

  @Override
  public UserResponse get(String sub) {
    UserDirectoryRepository.Row row =
        userDirectory
            .find(sub)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "User not found"));
    return toResponse(row, userDirectory.rolesFor(List.of(sub)).get(sub));
  }

  @Override
  @Transactional
  public void setStatus(String sub, UserStatus status) {
    Userinfo userinfo =
        userinfoRepository
            .findById(sub)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "User not found"));
    if (status == UserStatus.INVITED) {
      throw new IllegalArgumentException("An account becomes invited only by invitation.");
    }
    if (status == UserStatus.ACTIVE && UserStatus.INVITED.name().equals(userinfo.getStatus())) {
      throw new IllegalArgumentException(
          "This account is waiting for the person to activate it: resend the invitation.");
    }
    if (status == UserStatus.INACTIVE) {
      superAdminService.guardLastSuperAdmin(() -> saveStatus(userinfo, status));
      sessionService.endAll(sub, null, "account deactivated");
    } else {
      saveStatus(userinfo, status);
    }
    auditService.record(AuditAction.STATUS_CHANGED, sub, status.name());
  }

  @Override
  @Transactional
  public boolean changePassword(String sub, String currentPassword, String newPassword) {
    Userinfo userinfo =
        userinfoRepository
            .findById(sub)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "User not found"));
    if (!AccountType.LOCAL.name().equals(userinfo.getAccountType())) {
      throw new ApplicationException(
          Message.Application.ERROR, "This password is managed by your organisation");
    }
    PasswordPolicy.require(newPassword);
    UserDetails current = userDetailsManager.loadUserByUsername(sub);
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    if (currentPassword == null || !encoder.matches(currentPassword, current.getPassword())) {
      auditService.record(AuditAction.PASSWORD_CHANGE_FAILED, sub, "wrong current password");
      return false;
    }
    if (encoder.matches(newPassword, current.getPassword())) {
      throw new IllegalArgumentException("Choose a password different from the current one.");
    }
    userDirectory.setPassword(sub, encoder.encode(newPassword));
    // The browser that changed it stays signed in; every other device must sign in again.
    sessionService.endAll(sub, sessionService.currentWebHash(), "password changed");
    auditService.record(AuditAction.PASSWORD_CHANGED, sub, null);
    return true;
  }

  @Override
  @Transactional
  public void delete(String sub, String actor) {
    if (sub.equals(actor)) {
      throw new ApplicationException(
          Message.Application.ERROR, "You cannot delete your own account");
    }
    Userinfo userinfo =
        userinfoRepository
            .findById(sub)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "User not found"));
    if (!AccountType.LOCAL.name().equals(userinfo.getAccountType())) {
      throw new ApplicationException(
          Message.Application.ERROR, "Directory accounts cannot be deleted; deactivate instead");
    }
    if (!UserStatus.INACTIVE.name().equals(userinfo.getStatus())) {
      throw new ApplicationException(
          Message.Application.ERROR, "Deactivate the account before deleting it");
    }
    superAdminService.guardLastSuperAdmin(
        () -> {
          sessionService.endAll(sub, null, "account deleted");
          userDirectory.purge(sub, actor);
          auditService.record(AuditAction.USER_DELETED, sub, "sub stays reserved");
        });
  }

  private void saveStatus(Userinfo userinfo, UserStatus status) {
    userinfo.setStatus(status.name());
    userinfoRepository.saveAndFlush(userinfo);
  }

  private static String parseStatus(String status) {
    if (status == null || status.isBlank()) return null;
    try {
      return UserStatus.valueOf(status.trim().toUpperCase()).name();
    } catch (IllegalArgumentException ex) {
      throw new ApplicationException(Message.Application.ERROR, "Unknown status: " + status);
    }
  }

  /** The rules for a new local account: a non-company address, not a retired sub, a free email. */
  private void requireNewLocalAccount(String username) {
    emailDomainPolicy.requireGuestAddress(username, "Username");
    if (userDirectory.isTombstoned(username)) {
      throw new ApplicationException(
          Message.Application.ERROR, "That username belonged to a deleted account and is reserved");
    }
    requireFreeEmail(username, username);
  }

  /** Email is unique across accounts; the sign-in name (sub) is not affected by a change. */
  private void requireFreeEmail(String email, String sub) {
    if (userinfoRepository.existsByEmailIgnoreCaseAndSubNot(email.trim(), sub)) {
      throw new ApplicationException(
          Message.Application.ERROR, "That email is already used by another account");
    }
  }

  private static String blankToNull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }

  private static UserResponse toResponse(
      UserDirectoryRepository.Row r, List<UserDirectoryRepository.RoleRow> roles) {
    return UserResponse.builder()
        .sub(r.sub())
        .name(r.name())
        .email(r.email())
        .departmentId(r.departmentId())
        .unitId(r.unitId())
        .status(r.status())
        .accountType(r.accountType())
        .displayName(r.displayName())
        .dateOfBirth(r.dateOfBirth())
        .lastLoginAt(r.lastLoginAt())
        .createdAt(r.createdAt())
        .roles(
            (roles == null ? List.<UserDirectoryRepository.RoleRow>of() : roles)
                .stream()
                    .map(
                        x ->
                            UserResponse.RoleView.builder()
                                .projectId(x.projectId())
                                .role(x.role())
                                .build())
                    .toList())
        .build();
  }
}
