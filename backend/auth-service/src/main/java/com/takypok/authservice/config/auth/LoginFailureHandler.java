package com.takypok.authservice.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final StringRedisTemplate redisTemplate;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {

    String username = request.getParameter("username");
    if (username != null && !username.isBlank()) {
      try {
        String key = DomainAuthenticationManager.FAIL_KEY_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        int count = value != null ? Integer.parseInt(value) : 0;
        boolean locked = count >= DomainAuthenticationManager.MAX_ATTEMPTS;

        HttpSession session = request.getSession();
        session.setAttribute("loginLocked", locked);
        if (!locked) {
          session.setAttribute(
              "loginAttemptsLeft", DomainAuthenticationManager.MAX_ATTEMPTS - count);
        }
      } catch (Exception ex) {
        log.warn(
            "Redis unavailable — attempt info not stored for user={}: {}",
            username,
            ex.getMessage());
      }
    }

    response.sendRedirect(request.getContextPath() + "/login?error");
  }
}
