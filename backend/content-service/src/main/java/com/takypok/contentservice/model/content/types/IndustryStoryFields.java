package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A success-story card on the industry page. The first story's heading and figures fill the left
 * column.
 */
@Getter
@Setter
@ContentTypeKey("industry_story")
public class IndustryStoryFields implements ContentFields {
  /** Small label above the title (e.g. "BFSI"). */
  private String tag;

  @NotBlank private String title;
  private String body;
  private String image;
  private String linkLabel;
  private String linkUrl;

  /** Large heading in the left column (used on the first story). */
  private String heading;

  /** Up to four figures in the left column (used on the first story). */
  @Valid private List<StatItem> metrics;
}
