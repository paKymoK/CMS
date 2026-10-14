package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** The AI-assistant band under the hero: heading, prompt text and its two images. */
@Getter
@Setter
@ContentTypeKey("assistant_band")
public class AssistantBandFields implements ContentFields {
  @NotBlank private String title;

  /** Shown in the input bar until the visitor types. */
  @NotBlank private String greeting;

  /** First assistant message shown once the visitor starts a conversation. */
  private String welcome;

  /** Suggested questions shown as chips once the conversation starts. */
  private List<String> suggestions;

  private String backdropImage;
  private String orbImage;
}
