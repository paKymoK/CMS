package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.contentservice.model.content.blocks.PageBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockLibraryCreateRequest {
  @NotBlank
  @Pattern(regexp = "PAGE|BLOCK", message = "must be PAGE or BLOCK")
  private String kind;

  @NotBlank
  @Size(max = 120)
  private String name;

  @Size(max = 500)
  private String description;

  /** The blocks, checked like a page's own (known types, valid props, single-use blocks once). */
  @NotEmpty
  @Size(max = 20)
  @Valid
  private List<PageBlock> blocks;

  @JsonIgnore
  @AssertTrue(message = "a block entry holds exactly one block")
  public boolean isBlockKindHoldsOne() {
    return !"BLOCK".equals(kind) || blocks == null || blocks.size() == 1;
  }

  @JsonIgnore
  @AssertTrue(
      message =
          "case studies, industries, highlights, insights and the About sections may each appear only once")
  public boolean isSingleUseBlocksUnique() {
    return PageBlock.singleUseOnce(blocks);
  }
}
