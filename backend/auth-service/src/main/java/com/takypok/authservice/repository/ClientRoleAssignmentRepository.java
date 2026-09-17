package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.ClientRoleAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRoleAssignmentRepository
    extends JpaRepository<ClientRoleAssignment, String> {

  List<ClientRoleAssignment> findByRegisteredClientId(String registeredClientId);

  boolean existsByRegisteredClientIdAndUserSubAndProjectId(
      String registeredClientId, String userSub, String projectId);

  boolean existsByRegisteredClientIdAndGroupIdAndProjectId(
      String registeredClientId, String groupId, String projectId);

  /**
   * Resolves all roles for a user on a specific client — direct assignment UNION group assignments.
   * Runs at token issuance time.
   */
  @Query(
      """
      SELECT DISTINCT a.role FROM ClientRoleAssignment a
      WHERE a.registeredClientId = :clientId
        AND (
          a.userSub = :userSub
          OR a.groupId IN (
            SELECT m.groupId FROM UserGroupMember m WHERE m.userSub = :userSub
          )
        )
      """)
  List<String> findRolesForUserOnClient(
      @Param("clientId") String clientId, @Param("userSub") String userSub);

  /**
   * All assignments (direct + via group membership) for a user on a client, including project
   * scope. Used at token issuance to split roles into global vs per-project claims.
   */
  @Query(
      """
      SELECT a FROM ClientRoleAssignment a
      WHERE a.registeredClientId = :clientId
        AND (
          a.userSub = :userSub
          OR a.groupId IN (
            SELECT m.groupId FROM UserGroupMember m WHERE m.userSub = :userSub
          )
        )
      """)
  List<ClientRoleAssignment> findAssignmentsForUserOnClient(
      @Param("clientId") String clientId, @Param("userSub") String userSub);
}
