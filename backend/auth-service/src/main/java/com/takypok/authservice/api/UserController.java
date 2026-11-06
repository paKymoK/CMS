package com.takypok.authservice.api;

import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.InviteUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.request.UpdateUserStatusRequest;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.authservice.service.AccountMailService;
import com.takypok.authservice.service.UserService;
import com.takypok.core.model.PageResponse;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.Map;
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
  private final AccountMailService accountMail;

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

  /** Creates an account waiting for activation and emails the invitation. */
  @PostMapping("/invitations")
  public ResponseEntity<ResultMessage<Map<String, Boolean>>> invite(
      @RequestBody @Valid InviteUserRequest request) {
    requireMail();
    userService.createInvited(request.getUsername().trim(), request.getName());
    boolean sent = accountMail.sendInvitation(request.getUsername().trim());
    return ResponseEntity.ok(ResultMessage.success(Map.of("emailSent", sent)));
  }

  @PostMapping("/{sub}/invitation")
  public ResponseEntity<ResultMessage<Map<String, Boolean>>> resendInvitation(
      @PathVariable String sub) {
    requireMail();
    return ResponseEntity.ok(
        ResultMessage.success(Map.of("emailSent", accountMail.sendInvitation(sub))));
  }

  @PostMapping("/{sub}/password-reset")
  public ResponseEntity<ResultMessage<Map<String, Boolean>>> sendPasswordReset(
      @PathVariable String sub) {
    requireMail();
    return ResponseEntity.ok(
        ResultMessage.success(Map.of("emailSent", accountMail.sendPasswordReset(sub))));
  }

  private void requireMail() {
    if (!accountMail.isEnabled()) {
      throw new IllegalStateException("Email is not set up on this server.");
    }
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
