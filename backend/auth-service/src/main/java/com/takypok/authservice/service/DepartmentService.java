package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.Department;
import com.takypok.authservice.model.request.DepartmentCreateRequest;
import com.takypok.authservice.model.request.DepartmentUpdateRequest;
import java.util.List;

public interface DepartmentService {
  List<Department> get();

  Department getById(Long id);

  Department create(DepartmentCreateRequest request);

  Department update(DepartmentUpdateRequest request);

  void delete(Long id);
}
