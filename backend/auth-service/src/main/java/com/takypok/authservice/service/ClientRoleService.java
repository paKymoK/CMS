package com.takypok.authservice.service;

import com.takypok.authservice.model.request.ClientRoleAssignmentRequest;
import com.takypok.authservice.model.response.ClientRoleAssignmentResponse;
import java.util.List;

public interface ClientRoleService {

  List<ClientRoleAssignmentResponse> getAssignments(String registeredClientId);

  ClientRoleAssignmentResponse assign(
      String registeredClientId, ClientRoleAssignmentRequest request);

  void remove(String registeredClientId, String assignmentId);
}
