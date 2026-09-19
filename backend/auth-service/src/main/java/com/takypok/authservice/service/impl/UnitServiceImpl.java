package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.Unit;
import com.takypok.authservice.model.request.UnitCreateRequest;
import com.takypok.authservice.model.request.UnitUpdateRequest;
import com.takypok.authservice.repository.UnitRepository;
import com.takypok.authservice.service.UnitService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {
  private final UnitRepository unitRepository;

  @Override
  public List<Unit> get(Long departmentId) {
    if (departmentId != null) {
      return unitRepository.findByDepartmentIdOrderByName(departmentId);
    }
    return unitRepository.findAll().stream().sorted(Comparator.comparing(Unit::getName)).toList();
  }

  @Override
  public Unit getById(Long id) {
    return unitRepository
        .findById(id)
        .orElseThrow(() -> new ApplicationException(Message.Application.ERROR, "Unit not found"));
  }

  @Override
  public Unit create(UnitCreateRequest request) {
    Unit unit = new Unit();
    unit.setName(request.getName());
    unit.setDepartmentId(request.getDepartmentId());
    unit.setHead(request.getHead());
    unit.setLocation(request.getLocation());
    Unit saved = unitRepository.save(unit);
    return saved;
  }

  @Override
  public Unit update(UnitUpdateRequest request) {
    Unit unit = getById(request.getId());
    unit.setName(request.getName());
    unit.setDepartmentId(request.getDepartmentId());
    unit.setHead(request.getHead());
    unit.setLocation(request.getLocation());
      return unitRepository.save(unit);
  }

  @Override
  public void delete(Long id) {
    getById(id);
    unitRepository.deleteById(id);
  }
}
