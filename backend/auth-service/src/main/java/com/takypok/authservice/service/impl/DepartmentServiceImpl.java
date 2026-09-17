package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.Department;
import com.takypok.authservice.model.event.DepartmentEvent;
import com.takypok.authservice.model.request.DepartmentCreateRequest;
import com.takypok.authservice.model.request.DepartmentUpdateRequest;
import com.takypok.authservice.repository.DepartmentRepository;
import com.takypok.authservice.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {
  private final DepartmentRepository departmentRepository;
  private final KafkaTemplate<String, DepartmentEvent> kafkaTemplate;

  @Value("${department-events.topic}")
  private String topic;

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
    publish("CREATED", saved);
    return saved;
  }

  @Override
  public Department update(DepartmentUpdateRequest request) {
    Department department = getById(request.getId());
    department.setName(request.getName());
    department.setHead(request.getHead());
    department.setLocation(request.getLocation());
    Department saved = departmentRepository.save(department);
    publish("UPDATED", saved);
    return saved;
  }

  @Override
  public void delete(Long id) {
    Department department = getById(id);
    departmentRepository.deleteById(id);
    publish("DELETED", department);
  }

  private void publish(String eventType, Department department) {
    DepartmentEvent event =
        new DepartmentEvent(
            eventType,
            department.getId(),
            department.getName(),
            department.getHead(),
            department.getLocation());
    kafkaTemplate
        .send(topic, String.valueOf(department.getId()), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error(
                    "Failed to publish {} department event for id {}",
                    eventType,
                    department.getId(),
                    ex);
              }
            });
  }
}
