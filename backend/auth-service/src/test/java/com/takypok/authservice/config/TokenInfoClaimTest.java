package com.takypok.authservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.takypok.authservice.model.entity.Userinfo;
import org.junit.jupiter.api.Test;

class TokenInfoClaimTest {

  private static Userinfo person(String name, String displayName) {
    Userinfo u = new Userinfo();
    u.setSub("nttha5");
    u.setName(name);
    u.setDisplayName(displayName);
    u.setEmail("secret@corp.local");
    return u;
  }

  @Test
  void theDisplayNameIsShownWhenSet() {
    assertThat(
            CustomOAuth2TokenCustomizer.infoClaim(
                "nttha5", person("Nguyen Thi Thu Ha", " Ha (Marketing) ")))
        .containsEntry("sub", "nttha5")
        .containsEntry("name", "Ha (Marketing)");
  }

  @Test
  void theFullNameIsTheFallback() {
    assertThat(CustomOAuth2TokenCustomizer.infoClaim("nttha5", person("Nguyen Thi Thu Ha", "  ")))
        .containsEntry("name", "Nguyen Thi Thu Ha");
    assertThat(CustomOAuth2TokenCustomizer.infoClaim("nttha5", person("Nguyen Thi Thu Ha", null)))
        .containsEntry("name", "Nguyen Thi Thu Ha");
  }

  @Test
  void withNoNameAtAllOnlyTheSignInNameIdentifiesThem() {
    assertThat(CustomOAuth2TokenCustomizer.infoClaim("nttha5", person(null, null)))
        .containsOnlyKeys("sub");
    assertThat(CustomOAuth2TokenCustomizer.infoClaim("ghost", null)).containsOnlyKeys("sub");
  }

  @Test
  void nothingElsePersonalLeavesInTheClaim() {
    assertThat(CustomOAuth2TokenCustomizer.infoClaim("nttha5", person("A", "B")).keySet())
        .containsExactlyInAnyOrder("sub", "name"); // no email, department, avatar, date of birth
  }
}
