package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.Department;
import com.takypok.authservice.model.request.DepartmentCreateRequest;
import com.takypok.authservice.model.request.DepartmentUpdateRequest;
import com.takypok.authservice.repository.DepartmentRepository;
import com.takypok.authservice.service.DepartmentService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
  private final DepartmentRepository departmentRepository;

  @Override
  public List<Department> get() {
    return departmentRepository.findAll().stream()
        .sorted(Comparator.comparing(Department::getName))
        .toList();
  }

  @Override
  public Department getById(Long id) {
    return departmentRepository
        .findById(id)
        .orElseThrow(
            () -> new ApplicationException(Message.Application.ERROR, "Department not found"));
  }

  @Override
  public Department create(DepartmentCreateRequest request) {
    Department department = new Department();
    department.setName(request.getName());
    department.setHead(request.getHead());
    department.setLocation(request.getLocation());
    Department saved = departmentRepository.save(department);
    return saved;
  }

  @Override
  public Department update(DepartmentUpdateRequest request) {
    Department department = getById(request.getId());
    department.setName(request.getName());
    department.setHead(request.getHead());
    department.setLocation(request.getLocation());
    Department saved = departmentRepository.save(department);
    return saved;
  }

  @Override
  public void delete(Long id) {
    getById(id);
    departmentRepository.deleteById(id);
  }
}
