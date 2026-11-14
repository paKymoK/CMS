package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A category a post can belong to (Insights today). One per site and slug; Active is PUBLISHED and
 * Inactive is DRAFT. A category has at most one parent, itself a top-level category. The default
 * "Uncategorized" (slug {@code uncategorized}) holds posts that have no other category.
 */
@Getter
@Setter
@ContentTypeKey("post_category")
public class PostCategoryFields implements ContentFields {
  @NotBlank
  @Size(max = 100)
  private String name;

  /** The address segment (/category/{slug}); made from the name when left blank. */
  private String slug;

  @Size(max = 500)
  private String description;

  /** The parent category's id; empty for a top-level category. */
  private Long parentId;

  /**
   * Set only while the category is in the Trash: the posts and sub-categories to give back on
   * restore.
   */
  private List<Long> trashedPostIds;

  private List<Long> trashedChildIds;
}
