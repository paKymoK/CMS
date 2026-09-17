package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.UserGroup;
import com.takypok.authservice.model.entity.UserGroupMember;
import com.takypok.authservice.model.request.UserGroupRequest;
import com.takypok.authservice.model.response.GroupMemberResponse;
import com.takypok.authservice.model.response.UserGroupResponse;
import com.takypok.authservice.repository.UserGroupMemberRepository;
import com.takypok.authservice.repository.UserGroupRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.authservice.service.GroupService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final UserGroupRepository groupRepository;
  private final UserGroupMemberRepository memberRepository;
  private final UserinfoRepository userinfoRepository;

  @Override
  public List<UserGroupResponse> getAll() {
    return groupRepository.findAll().stream()
        .map(g -> toResponse(g, getMembers(g.getId())))
        .toList();
  }

  @Override
  public UserGroupResponse getById(String id) {
    UserGroup group = findOrThrow(id);
    return toResponse(group, getMembers(id));
  }

  @Override
  public UserGroupResponse create(UserGroupRequest request) {
    UserGroup group =
        new UserGroup(UUID.randomUUID().toString(), request.getName(), request.getDescription());
    groupRepository.save(group);
    return toResponse(group, List.of());
  }

  @Override
  public UserGroupResponse update(String id, UserGroupRequest request) {
    UserGroup group = findOrThrow(id);
    group.setName(request.getName());
    group.setDescription(request.getDescription());
    groupRepository.save(group);
    return toResponse(group, getMembers(id));
  }

  @Override
  @Transactional
  public void delete(String id) {
    findOrThrow(id);
    groupRepository.deleteById(id);
  }

  @Override
  public List<GroupMemberResponse> getMembers(String groupId) {
    return memberRepository.findByGroupId(groupId).stream()
        .map(
            m -> {
              var info = userinfoRepository.getBySub(m.getUserSub());
              return GroupMemberResponse.builder()
                  .sub(m.getUserSub())
                  .name(info != null ? info.getName() : m.getUserSub())
                  .email(info != null ? info.getEmail() : null)
                  .build();
            })
        .toList();
  }

  @Override
  public void addMember(String groupId, String userSub) {
    findOrThrow(groupId);
    if (memberRepository.existsByGroupIdAndUserSub(groupId, userSub)) return;
    memberRepository.save(new UserGroupMember(groupId, userSub));
  }

  @Override
  @Transactional
  public void removeMember(String groupId, String userSub) {
    findOrThrow(groupId);
    memberRepository.deleteByGroupIdAndUserSub(groupId, userSub);
  }

  private UserGroup findOrThrow(String id) {
    return groupRepository
        .findById(id)
        .orElseThrow(() -> new ApplicationException(Message.Application.ERROR, "Group not found"));
  }

  private UserGroupResponse toResponse(UserGroup g, List<GroupMemberResponse> members) {
    return UserGroupResponse.builder()
        .id(g.getId())
        .name(g.getName())
        .description(g.getDescription())
        .members(members)
        .build();
  }
}
