package com.takypok.authservice.service;

import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;

public interface UserService {
  void create(CreateUserRequest request);

  void updateProfile(String sub, UpdateUserProfileRequest request);
}
