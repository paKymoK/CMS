package com.takypok.chatservice.util;

public final class TextUtils {

  private TextUtils() {}

  public static String stripThinkingTokens(String response) {
    if (response == null) return "";
    return response.replaceAll("(?s)<think>.*?</think>", "").trim();
  }
}
