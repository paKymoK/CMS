package com.takypok.authservice.api;

import com.takypok.authservice.model.request.ClientRoleAssignmentRequest;
import com.takypok.authservice.model.response.ClientRoleAssignmentResponse;
import com.takypok.authservice.service.ClientRoleService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clients/{clientId}/roles")
public class ClientRoleController {

  private final ClientRoleService clientRoleService;

  @GetMapping
  public ResponseEntity<ResultMessage<List<ClientRoleAssignmentResponse>>> getAssignments(
      @PathVariable String clientId) {
    return ResponseEntity.ok(ResultMessage.success(clientRoleService.getAssignments(clientId)));
  }

  @PostMapping
  public ResponseEntity<ResultMessage<ClientRoleAssignmentResponse>> assign(
      @PathVariable String clientId, @RequestBody @Valid ClientRoleAssignmentRequest request) {
    return ResponseEntity.ok(ResultMessage.success(clientRoleService.assign(clientId, request)));
  }

  @DeleteMapping("/{assignmentId}")
  public ResponseEntity<ResultMessage<Void>> remove(
      @PathVariable String clientId, @PathVariable String assignmentId) {
    clientRoleService.remove(clientId, assignmentId);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
