package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.Unit;
import com.takypok.authservice.model.request.UnitCreateRequest;
import com.takypok.authservice.model.request.UnitUpdateRequest;
import java.util.List;

public interface UnitService {
  List<Unit> get(Long departmentId);

  Unit getById(Long id);

  Unit create(UnitCreateRequest request);

  Unit update(UnitUpdateRequest request);

  void delete(Long id);
}
