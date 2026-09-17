package com.takypok.authservice.config.auth;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainAuthenticationManager implements AuthenticationManager {

  static final String FAIL_KEY_PREFIX = "login-fail:";
  static final int MAX_ATTEMPTS = 5;
  private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(10);

  // Username-only keying (above) lets anyone lock out a KNOWN username (e.g. "admin") from
  // anywhere, with no need to control an IP — a free account-lockout DoS. This second, IP-keyed
  // counter closes that gap. Threshold is much higher than MAX_ATTEMPTS since one IP (NAT,
  // shared office network) can legitimately represent many real users failing occasionally.
  private static final String IP_FAIL_KEY_PREFIX = "login-fail-ip:";
  private static final int MAX_ATTEMPTS_PER_IP = 20;

  private final LdapAuthenticationProvider ldapAuthenticationProvider;
  private final DaoAuthenticationProvider jdbcAuthenticationProvider;
  private final JdbcUserDetailsManager jdbcUserDetailsManager;
  private final StringRedisTemplate redisTemplate;

  @Override
  public Authentication authenticate(Authentication authentication) {
    DomainAuthenticationToken token = (DomainAuthenticationToken) authentication;
    String username = token.getName();
    String remoteAddress = token.getRemoteAddress();

    checkLocked(username, remoteAddress);

    Authentication result;
    try {
      result =
          switch (token.getDomain()) {
            case "INTERNAL" ->
                isJdbcAdmin(username)
                    ? jdbcAuthenticationProvider.authenticate(token)
                    : ldapAuthenticationProvider.authenticate(token);
            case "GUEST" -> jdbcAuthenticationProvider.authenticate(token);
            default -> throw new BadCredentialsException("Unknown domain: " + token.getDomain());
          };
    } catch (AuthenticationException ex) {
      recordFailure(username, remoteAddress);
      throw ex;
    }

    clearFailures(username, remoteAddress);

    if (result instanceof AbstractAuthenticationToken aat) {
      aat.setDetails(token.getDomain());
    }
    return result;
  }

  private void checkLocked(String username, String remoteAddress) {
    try {
      String value = redisTemplate.opsForValue().get(FAIL_KEY_PREFIX + username);
      if (value != null && Integer.parseInt(value) >= MAX_ATTEMPTS) {
        throw new LockedException("Account temporarily locked due to too many failed attempts");
      }
      String ipValue = redisTemplate.opsForValue().get(IP_FAIL_KEY_PREFIX + remoteAddress);
      if (ipValue != null && Integer.parseInt(ipValue) >= MAX_ATTEMPTS_PER_IP) {
        throw new LockedException("Too many failed login attempts from this network");
      }
    } catch (LockedException ex) {
      throw ex;
    } catch (Exception ex) {
      log.warn("Redis unavailable — lock check skipped for user={}: {}", username, ex.getMessage());
    }
  }

  private void recordFailure(String username, String remoteAddress) {
    try {
      String key = FAIL_KEY_PREFIX + username;
      Long count = redisTemplate.opsForValue().increment(key);
      if (count != null && count == 1) {
        redisTemplate.expire(key, LOCKOUT_DURATION);
      }
      log.debug("Failed login attempt {}/{} for user={}", count, MAX_ATTEMPTS, username);

      String ipKey = IP_FAIL_KEY_PREFIX + remoteAddress;
      Long ipCount = redisTemplate.opsForValue().increment(ipKey);
      if (ipCount != null && ipCount == 1) {
        redisTemplate.expire(ipKey, LOCKOUT_DURATION);
      }
      log.debug(
          "Failed login attempt {}/{} from ip={}", ipCount, MAX_ATTEMPTS_PER_IP, remoteAddress);
    } catch (Exception ex) {
      log.warn(
          "Redis unavailable — failure count not recorded for user={}: {}",
          username,
          ex.getMessage());
    }
  }

  private void clearFailures(String username, String remoteAddress) {
    try {
      redisTemplate.delete(FAIL_KEY_PREFIX + username);
      redisTemplate.delete(IP_FAIL_KEY_PREFIX + remoteAddress);
    } catch (Exception ex) {
      log.warn(
          "Redis unavailable — failure count not cleared for user={}: {}",
          username,
          ex.getMessage());
    }
  }

  private boolean isJdbcAdmin(String username) {
    if (!jdbcUserDetailsManager.userExists(username)) return false;
    UserDetails user = jdbcUserDetailsManager.loadUserByUsername(username);
    return user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
  }
}
