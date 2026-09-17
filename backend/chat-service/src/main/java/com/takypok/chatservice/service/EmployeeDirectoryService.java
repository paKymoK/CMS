package com.takypok.chatservice.service;

import com.takypok.core.model.authentication.User;
import reactor.core.publisher.Mono;

public interface EmployeeDirectoryService {
  Mono<User> getUser(String sub);

  /**
   * Resolves the acting user against the local directory instead of the JWT "info" claim, so
   * stamped fields (message sender, typing-indicator name) reflect current employee-service data
   * rather than a snapshot frozen at login time. Falls back to a sub-only user if the directory
   * hasn't caught up yet.
   */
  default Mono<User> resolveActingUser(String sub) {
    return getUser(sub).defaultIfEmpty(new User(sub, null, null, null, null, null));
  }
}
