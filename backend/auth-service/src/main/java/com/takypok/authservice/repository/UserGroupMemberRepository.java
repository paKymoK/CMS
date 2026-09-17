package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.UserGroupMember;
import com.takypok.authservice.model.entity.UserGroupMemberId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupMemberRepository
    extends JpaRepository<UserGroupMember, UserGroupMemberId> {

  List<UserGroupMember> findByGroupId(String groupId);

  void deleteByGroupIdAndUserSub(String groupId, String userSub);

  boolean existsByGroupIdAndUserSub(String groupId, String userSub);

  @Query("SELECT m.groupId FROM UserGroupMember m WHERE m.userSub = :userSub")
  List<String> findGroupIdsByUserSub(@Param("userSub") String userSub);
}
