package com.takypok.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.takypok.core.Constants;
import com.takypok.core.model.authentication.User;
import io.jsonwebtoken.lang.Strings;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Slf4j
public class JwtUtil {
  private JwtUtil() {
    throw new UnsupportedOperationException();
  }

  public static String extractAccessToken(ServerHttpRequest request) {
    return Optional.ofNullable(request.getHeaders().get(Constants.AUTHENTICATION_HEADER))
        .filter(
            bearers ->
                Strings.startsWithIgnoreCase(bearers.get(0), Constants.AUTHENTICATION_PREFIX))
        .map((headerValues) -> headerValues.get(0).replace(Constants.AUTHENTICATION_PREFIX, ""))
        .orElse(null);
  }

  public static User extractUserFromAccessToken(String token) {
    return Optional.ofNullable(tryDecoding(token))
        .map(JwtUtil::getUserFromAccessToken)
        .orElse(null);
  }

  private static DecodedJWT tryDecoding(String token) {
    try {
      return JWT.decode(token);
    } catch (JWTDecodeException e) {
      log.error("Unable to Decoding token. {}", e.getMessage());
      return null;
    }
  }

  private static User getUserFromAccessToken(DecodedJWT jwt) {
    try {
      User user = new User();
      // TODO: modify token here !!!!
      user.setName(jwt.getClaim("name").asString());
      return user;
    } catch (Exception ex) {
      log.error("Unable to parse AccessToken. {}", ex.getMessage());
      return null;
    }
  }
}
