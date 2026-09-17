package com.takypok.authservice.service;

import com.takypok.authservice.model.request.UserGroupRequest;
import com.takypok.authservice.model.response.GroupMemberResponse;
import com.takypok.authservice.model.response.UserGroupResponse;
import java.util.List;

public interface GroupService {

  List<UserGroupResponse> getAll();

  UserGroupResponse getById(String id);

  UserGroupResponse create(UserGroupRequest request);

  UserGroupResponse update(String id, UserGroupRequest request);

  void delete(String id);

  List<GroupMemberResponse> getMembers(String groupId);

  void addMember(String groupId, String userSub);

  void removeMember(String groupId, String userSub);
}
