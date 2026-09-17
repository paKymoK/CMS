package com.takypok.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.core.Constants;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogUtil {
  private static ObjectMapper om;

  private final ObjectMapper mapper;

  @PostConstruct
  public void init() {
    om = mapper;
  }

  public static List<String> getExceptionMessageChain(Throwable throwable) {
    List<String> result = new ArrayList<>();
    while (throwable != null) {
      result.add(throwable.getMessage());
      throwable = throwable.getCause();
    }
    return result; // ["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
  }

  public static void logObject(String function, Object object) {
    logObject(function, object, Level.INFO);
  }

  public static void logObject(String function, Object object, Level level) {
    try {

      String jsonString = om.writeValueAsString(object);
      log.atLevel(level).log("{} {}", function, jsonString);

    } catch (Exception ex) {
      log.error("Error while logging Object", ex);
    }
  }

  public static void logString(String function, String string) {
    logString(function, string, Level.INFO);
  }

  public static void logString(String function, String string, Level level) {
    try {
      log.atLevel(level).log("{} {}", function, string);
    } catch (Exception ex) {
      log.error("Error while logging String", ex);
    }
  }

  public static String getRequestId() {
    return MDC.get(Constants.X_REQUEST_ID) == null
        ? UUID.randomUUID().toString()
        : MDC.get(Constants.X_REQUEST_ID);
  }
}
