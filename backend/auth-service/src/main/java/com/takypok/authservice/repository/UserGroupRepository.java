package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, String> {}
