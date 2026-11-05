package com.takypok.authservice.api;

import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.request.UpdateUserStatusRequest;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.authservice.service.UserService;
import com.takypok.core.model.PageResponse;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ResultMessage<PageResponse<UserResponse>>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String project,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        ResultMessage.success(userService.search(q, status, role, project, page, size)));
  }

  @GetMapping("/{sub}")
  public ResponseEntity<ResultMessage<UserResponse>> get(@PathVariable String sub) {
    return ResponseEntity.ok(ResultMessage.success(userService.get(sub)));
  }

  @PatchMapping("/{sub}/status")
  public ResponseEntity<ResultMessage<Void>> updateStatus(
      @PathVariable String sub, @RequestBody @Valid UpdateUserStatusRequest request) {
    userService.setStatus(sub, request.getStatus());
    return ResponseEntity.ok(ResultMessage.success(null));
  }

  @DeleteMapping("/{sub}")
  public ResponseEntity<ResultMessage<Void>> delete(
      @PathVariable String sub, Authentication authentication) {
    userService.delete(sub, authentication.getName());
    return ResponseEntity.ok(ResultMessage.success(null));
  }

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
