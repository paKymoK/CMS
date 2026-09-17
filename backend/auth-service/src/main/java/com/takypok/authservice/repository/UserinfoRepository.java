package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.Userinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserinfoRepository extends JpaRepository<Userinfo, String> {

  @Query(value = "SELECT * FROM userinfo WHERE sub = :sub", nativeQuery = true)
  Userinfo getBySub(String sub);
}
