package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.core.model.PageResponse;

public interface UserService {
  void create(CreateUserRequest request);

  /**
   * Creates a local account with no usable password, waiting for the person to activate it through
   * an emailed link (see AccountMailService). It cannot sign in until then.
   */
  void createInvited(String username, String name);

  void updateProfile(String sub, UpdateUserProfileRequest request);

  PageResponse<UserResponse> search(
      String q, String status, String role, String projectId, int page, int size);

  UserResponse get(String sub);

  /**
   * Changes the password of a LOCAL account after checking the current one. A wrong current
   * password is audited and returns false (it throws nothing, so the audit row is kept); other
   * problems throw. A change also drops the user's refresh tokens, so other sessions must sign in
   * again.
   */
  boolean changePassword(String sub, String currentPassword, String newPassword);

  /** Deactivating also revokes the user's refresh tokens. The last active Super Admin is kept. */
  void setStatus(String sub, UserStatus status);

  /**
   * Erases a LOCAL account that is already INACTIVE; its sub stays reserved so it can never be
   * handed to someone else. Directory accounts, one's own account and an account that would leave
   * no active Super Admin are refused.
   */
  void delete(String sub, String actor);
}
