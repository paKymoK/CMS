package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserAgentsTest {

  @Test
  void commonBrowsersAreNamedWithTheirSystem() {
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/126.0.0.0 Safari/537.36"))
        .isEqualTo("Chrome on macOS");
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"))
        .isEqualTo("Firefox on Windows");
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15"
                    + " (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1"))
        .isEqualTo("Safari on iOS");
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/126.0.0.0 Mobile Safari/537.36"))
        .isEqualTo("Chrome on Android");
  }

  @Test
  void edgeAndOperaAreNotMistakenForChrome() {
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0"))
        .isEqualTo("Edge on Windows");
    assertThat(
            UserAgents.describe(
                "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 Chrome/126.0 Safari/537.36 OPR/110.0"))
        .isEqualTo("Opera on Windows");
  }

  @Test
  void missingOrUnknownAgentsAreHandled() {
    assertThat(UserAgents.describe(null)).isEqualTo("Unknown device");
    assertThat(UserAgents.describe("  ")).isEqualTo("Unknown device");
    assertThat(UserAgents.describe("curl/8.4.0")).isEqualTo("Script");
    assertThat(UserAgents.describe("SomethingNew/1.0")).isEqualTo("Browser");
  }
}
