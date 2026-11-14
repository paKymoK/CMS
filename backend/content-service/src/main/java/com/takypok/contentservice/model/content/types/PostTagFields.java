package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A tag a post can carry. One per site and slug; Active is PUBLISHED and Inactive is DRAFT. */
@Getter
@Setter
@ContentTypeKey("post_tag")
public class PostTagFields implements ContentFields {
  @NotBlank
  @Size(max = 100)
  private String name;

  /** The address segment (/tag/{slug}); made from the name when left blank. */
  private String slug;

  @Size(max = 500)
  private String description;

  /** Set only while the tag is in the Trash: the posts to give back on restore. */
  private List<Long> trashedPostIds;
}
