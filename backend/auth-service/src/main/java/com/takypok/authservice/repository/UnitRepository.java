package com.takypok.authservice.repository;

import com.takypok.authservice.model.entity.Unit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
  List<Unit> findByDepartmentIdOrderByName(Long departmentId);
}
