package com.takypok.chatservice.service.impl;

import com.takypok.chatservice.model.entity.EmployeeDirectory;
import com.takypok.chatservice.repository.EmployeeDirectoryRepository;
import com.takypok.chatservice.service.EmployeeDirectoryService;
import com.takypok.core.model.authentication.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmployeeDirectoryServiceImpl implements EmployeeDirectoryService {
  private final EmployeeDirectoryRepository employeeDirectoryRepository;

  @Override
  public Mono<User> getUser(String sub) {
    return employeeDirectoryRepository.findById(sub).map(this::toUser);
  }

  private User toUser(EmployeeDirectory directory) {
    return new User(
        directory.getSub(),
        directory.getName(),
        directory.getEmail(),
        directory.getTitle(),
        directory.getDepartment(),
        directory.getAvatarUrl());
  }
}
