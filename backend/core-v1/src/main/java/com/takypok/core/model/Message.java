package com.takypok.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record Message(String code, String message) {

  @AllArgsConstructor
  @Getter
  public enum Application {
    SUCCESS("TAK-SUC", "Success"),
    UNKNOWN_ERROR("TAK-ERR", "Unknown Error, Please contact administrator !!!"),
    ERROR("TAK-ERR", "{}"),
    ;
    public final String code;
    public final String message;
  }

  public static Message get(Application application, String... args) {
    return new Message(application.code, handleMessage(application.message, args));
  }

  private static String handleMessage(String format, String... args) {
    for (String str : args) {
      try {
        format = format.replaceFirst("\\{}", str);
      } catch (Exception ignored) {

      }
    }
    return format;
  }
}
