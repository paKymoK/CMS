package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.ClientSessionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSessionPolicyRepository extends JpaRepository<ClientSessionPolicy, String> {}
