package com.takypok.core.config;

import com.takypok.core.model.authentication.User;
import com.takypok.core.util.AuthenticationUtil;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Configuration
public class AuditingConfig {

  private static final User SYSTEM = new User("system", "System", null, null, null, null);

  @Bean
  public ReactiveAuditorAware<User> auditorAware() {
    return () ->
        ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(
                auth -> {
                  try {
                    User user = AuthenticationUtil.getUserInfo(auth);
                    return user != null ? user : SYSTEM;
                  } catch (Exception e) {
                    return SYSTEM;
                  }
                })
            .onErrorReturn(SYSTEM)
            .switchIfEmpty(Mono.just(SYSTEM));
  }

  @Bean
  public DateTimeProvider auditingDateTimeProvider() {
    return () -> Optional.of(ZonedDateTime.now(ZoneId.systemDefault()));
  }
}
