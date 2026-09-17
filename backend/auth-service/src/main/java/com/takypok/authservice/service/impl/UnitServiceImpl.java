package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.Unit;
import com.takypok.authservice.model.event.UnitEvent;
import com.takypok.authservice.model.request.UnitCreateRequest;
import com.takypok.authservice.model.request.UnitUpdateRequest;
import com.takypok.authservice.repository.UnitRepository;
import com.takypok.authservice.service.UnitService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitServiceImpl implements UnitService {
  private final UnitRepository unitRepository;
  private final KafkaTemplate<String, UnitEvent> kafkaTemplate;

  @Value("${unit-events.topic}")
  private String topic;

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
    publish("CREATED", saved);
    return saved;
  }

  @Override
  public Unit update(UnitUpdateRequest request) {
    Unit unit = getById(request.getId());
    unit.setName(request.getName());
    unit.setDepartmentId(request.getDepartmentId());
    unit.setHead(request.getHead());
    unit.setLocation(request.getLocation());
    Unit saved = unitRepository.save(unit);
    publish("UPDATED", saved);
    return saved;
  }

  @Override
  public void delete(Long id) {
    Unit unit = getById(id);
    unitRepository.deleteById(id);
    publish("DELETED", unit);
  }

  private void publish(String eventType, Unit unit) {
    UnitEvent event =
        new UnitEvent(
            eventType,
            unit.getId(),
            unit.getName(),
            unit.getDepartmentId(),
            unit.getHead(),
            unit.getLocation());
    kafkaTemplate
        .send(topic, String.valueOf(unit.getId()), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish {} unit event for id {}", eventType, unit.getId(), ex);
              }
            });
  }
}
