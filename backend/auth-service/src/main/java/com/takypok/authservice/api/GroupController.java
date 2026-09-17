package com.takypok.authservice.api;

import com.takypok.authservice.model.request.UserGroupRequest;
import com.takypok.authservice.model.response.GroupMemberResponse;
import com.takypok.authservice.model.response.UserGroupResponse;
import com.takypok.authservice.service.GroupService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/groups")
public class GroupController {

  private final GroupService groupService;

  @GetMapping
  public ResponseEntity<ResultMessage<List<UserGroupResponse>>> getAll() {
    return ResponseEntity.ok(ResultMessage.success(groupService.getAll()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResultMessage<UserGroupResponse>> getById(@PathVariable String id) {
    return ResponseEntity.ok(ResultMessage.success(groupService.getById(id)));
  }

  @PostMapping
  public ResponseEntity<ResultMessage<UserGroupResponse>> create(
      @RequestBody @Valid UserGroupRequest request) {
    return ResponseEntity.ok(ResultMessage.success(groupService.create(request)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResultMessage<UserGroupResponse>> update(
      @PathVariable String id, @RequestBody @Valid UserGroupRequest request) {
    return ResponseEntity.ok(ResultMessage.success(groupService.update(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResultMessage<Void>> delete(@PathVariable String id) {
    groupService.delete(id);
    return ResponseEntity.ok(ResultMessage.success(null));
  }

  @GetMapping("/{id}/members")
  public ResponseEntity<ResultMessage<List<GroupMemberResponse>>> getMembers(
      @PathVariable String id) {
    return ResponseEntity.ok(ResultMessage.success(groupService.getMembers(id)));
  }

  @PostMapping("/{id}/members")
  public ResponseEntity<ResultMessage<Void>> addMember(
      @PathVariable String id, @RequestBody Map<String, String> body) {
    groupService.addMember(id, body.get("userSub"));
    return ResponseEntity.ok(ResultMessage.success(null));
  }

  @DeleteMapping("/{id}/members/{userSub}")
  public ResponseEntity<ResultMessage<Void>> removeMember(
      @PathVariable String id, @PathVariable String userSub) {
    groupService.removeMember(id, userSub);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
