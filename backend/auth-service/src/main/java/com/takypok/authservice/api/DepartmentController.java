package com.takypok.authservice.api;

import com.takypok.authservice.model.entity.Department;
import com.takypok.authservice.model.request.DepartmentCreateRequest;
import com.takypok.authservice.model.request.DepartmentUpdateRequest;
import com.takypok.authservice.service.DepartmentService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/v1/departments")
public class DepartmentController {
  private final DepartmentService departmentService;

  @GetMapping
  public ResponseEntity<ResultMessage<List<Department>>> get() {
    return ResponseEntity.ok(ResultMessage.success(departmentService.get()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResultMessage<Department>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ResultMessage.success(departmentService.getById(id)));
  }

  @PostMapping
  public ResponseEntity<ResultMessage<Department>> create(
      @RequestBody @Valid DepartmentCreateRequest request) {
    return ResponseEntity.ok(ResultMessage.success(departmentService.create(request)));
  }

  @PutMapping
  public ResponseEntity<ResultMessage<Department>> update(
      @RequestBody @Valid DepartmentUpdateRequest request) {
    return ResponseEntity.ok(ResultMessage.success(departmentService.update(request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResultMessage<Void>> delete(@PathVariable Long id) {
    departmentService.delete(id);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
