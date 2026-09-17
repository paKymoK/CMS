package com.takypok.authservice.api;

import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.service.UserService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
  private final UserService userService;

  @PostMapping
  public ResponseEntity<ResultMessage<Void>> create(@RequestBody @Valid CreateUserRequest request) {
    userService.create(request);
    return ResponseEntity.ok(ResultMessage.success(null));
  }

  @PatchMapping("/{sub}/profile")
  public ResponseEntity<ResultMessage<Void>> updateProfile(
      @PathVariable String sub, @RequestBody UpdateUserProfileRequest request) {
    userService.updateProfile(sub, request);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
