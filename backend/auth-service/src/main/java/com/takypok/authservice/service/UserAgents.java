package com.takypok.authservice.service;

/**
 * A short, human label for a browser's User-Agent ("Chrome on macOS"). Good enough to tell one's
 * own devices apart; it is a description, never used for any decision.
 */
public final class UserAgents {
  private UserAgents() {}

  public static String describe(String userAgent) {
    if (userAgent == null || userAgent.isBlank()) return "Unknown device";
    String ua = userAgent;
    String browser;
    // Order matters: Edge and Opera also say Chrome, Chrome also says Safari.
    if (ua.contains("Edg/") || ua.contains("EdgA/") || ua.contains("EdgiOS/")) browser = "Edge";
    else if (ua.contains("OPR/") || ua.contains("Opera")) browser = "Opera";
    else if (ua.contains("Firefox/") || ua.contains("FxiOS/")) browser = "Firefox";
    else if (ua.contains("Chrome/") || ua.contains("CriOS/") || ua.contains("HeadlessChrome/"))
      browser = "Chrome";
    else if (ua.contains("Safari/") && ua.contains("Version/")) browser = "Safari";
    else if (ua.startsWith("curl/") || ua.startsWith("python") || ua.contains("Python-urllib"))
      browser = "Script";
    else browser = "Browser";

    String os;
    if (ua.contains("Windows")) os = "Windows";
    else if (ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")) os = "iOS";
    else if (ua.contains("Android")) os = "Android";
    else if (ua.contains("Mac OS X") || ua.contains("Macintosh")) os = "macOS";
    else if (ua.contains("CrOS")) os = "ChromeOS";
    else if (ua.contains("Linux") || ua.contains("X11")) os = "Linux";
    else os = null;
    return os == null ? browser : browser + " on " + os;
  }
}
