package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.ClientRoleAssignment;
import com.takypok.authservice.model.entity.UserGroup;
import com.takypok.authservice.model.request.ClientRoleAssignmentRequest;
import com.takypok.authservice.model.response.ClientRoleAssignmentResponse;
import com.takypok.authservice.repository.ClientRoleAssignmentRepository;
import com.takypok.authservice.repository.UserGroupRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.authservice.service.ClientRoleService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientRoleServiceImpl implements ClientRoleService {

  private final ClientRoleAssignmentRepository assignmentRepository;
  private final UserinfoRepository userinfoRepository;
  private final UserGroupRepository groupRepository;

  @Override
  public List<ClientRoleAssignmentResponse> getAssignments(String registeredClientId) {
    return assignmentRepository.findByRegisteredClientId(registeredClientId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ClientRoleAssignmentResponse assign(
      String registeredClientId, ClientRoleAssignmentRequest request) {

    boolean isUser = request.getUserSub() != null && !request.getUserSub().isBlank();
    boolean isGroup = request.getGroupId() != null && !request.getGroupId().isBlank();

    if (isUser == isGroup) {
      throw new ApplicationException(
          Message.Application.ERROR, "Exactly one of userSub or groupId must be provided");
    }

    if (isUser
        && assignmentRepository.existsByRegisteredClientIdAndUserSubAndProjectId(
            registeredClientId, request.getUserSub(), request.getProjectId())) {
      throw new ApplicationException(
          Message.Application.ERROR, "User already has a role on this project");
    }
    if (isGroup
        && assignmentRepository.existsByRegisteredClientIdAndGroupIdAndProjectId(
            registeredClientId, request.getGroupId(), request.getProjectId())) {
      throw new ApplicationException(
          Message.Application.ERROR, "Group already has a role on this project");
    }

    ClientRoleAssignment assignment =
        ClientRoleAssignment.builder()
            .id(UUID.randomUUID().toString())
            .registeredClientId(registeredClientId)
            .userSub(isUser ? request.getUserSub() : null)
            .groupId(isGroup ? request.getGroupId() : null)
            .projectId(request.getProjectId())
            .role(request.getRole())
            .build();

    assignmentRepository.save(assignment);
    return toResponse(assignment);
  }

  @Override
  public void remove(String registeredClientId, String assignmentId) {
    ClientRoleAssignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(
                () -> new ApplicationException(Message.Application.ERROR, "Assignment not found"));
    if (!assignment.getRegisteredClientId().equals(registeredClientId)) {
      throw new ApplicationException(Message.Application.ERROR, "Assignment not found");
    }
    assignmentRepository.deleteById(assignmentId);
  }

  private ClientRoleAssignmentResponse toResponse(ClientRoleAssignment a) {
    boolean isUser = a.getUserSub() != null;
    String subjectName;
    if (isUser) {
      var info = userinfoRepository.getBySub(a.getUserSub());
      subjectName = info != null ? info.getName() : a.getUserSub();
    } else {
      subjectName =
          groupRepository.findById(a.getGroupId()).map(UserGroup::getName).orElse(a.getGroupId());
    }
    return ClientRoleAssignmentResponse.builder()
        .id(a.getId())
        .registeredClientId(a.getRegisteredClientId())
        .type(isUser ? "USER" : "GROUP")
        .subjectId(isUser ? a.getUserSub() : a.getGroupId())
        .subjectName(subjectName)
        .projectId(a.getProjectId())
        .role(a.getRole())
        .build();
  }
}
