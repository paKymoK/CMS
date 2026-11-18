package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * One block of a page, stored as {@code {id, type, hidden, props}}. {@code type} picks the class of
 * {@code props}; a type not listed here cannot be stored, and unknown props are dropped like on
 * every other content type. Service and industry pages share this one list, so a block can be moved
 * or saved to the library and used on any of them (Service, Industry and About pages).
 */
@Getter
@Setter
public class PageBlock {
  /** Chosen by the editor so a block keeps its identity while it is moved. */
  @NotBlank
  @Pattern(
      regexp = "^[A-Za-z0-9_-]{1,64}$",
      message = "use letters, digits, dashes and underscores")
  private String id;

  /** Kept in the page but not shown on the website. */
  private Boolean hidden;

  @NotNull
  @Valid
  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = CapabilitiesBlock.class, name = "capabilities"),
    @JsonSubTypes.Type(value = ProcessBlock.class, name = "process"),
    @JsonSubTypes.Type(value = CaseStudiesBlock.class, name = "case-studies"),
    @JsonSubTypes.Type(value = ReasonsBlock.class, name = "reasons-slider"),
    @JsonSubTypes.Type(value = IndustriesBlock.class, name = "industries"),
    @JsonSubTypes.Type(value = HighlightsBlock.class, name = "highlights"),
    @JsonSubTypes.Type(value = SolutionsBlock.class, name = "solutions"),
    @JsonSubTypes.Type(value = IndustryReasonsBlock.class, name = "reasons-cards"),
    @JsonSubTypes.Type(value = IndustryStoriesBlock.class, name = "stories"),
    @JsonSubTypes.Type(value = InsightsBlock.class, name = "insights"),
    @JsonSubTypes.Type(value = IntroBlock.class, name = "intro"),
    @JsonSubTypes.Type(value = VisionBlock.class, name = "vision"),
    @JsonSubTypes.Type(value = MissionBlock.class, name = "mission"),
    @JsonSubTypes.Type(value = LeadersBlock.class, name = "leaders"),
    @JsonSubTypes.Type(value = ValuesBlock.class, name = "values")
  })
  private BlockProps props;

  /** Blocks that read site-wide content or fill a fixed layout: at most one per page. */
  private static boolean singleUse(BlockProps p) {
    return p instanceof CaseStudiesBlock
        || p instanceof IndustriesBlock
        || p instanceof HighlightsBlock
        || p instanceof InsightsBlock
        || p instanceof SingleUseBlock;
  }

  public static boolean idsUnique(List<PageBlock> blocks) {
    return blocks == null
        || blocks.stream().map(PageBlock::getId).distinct().count() == blocks.size();
  }

  public static boolean singleUseOnce(List<PageBlock> blocks) {
    return blocks == null
        || blocks.stream()
                .map(PageBlock::getProps)
                .filter(PageBlock::singleUse)
                .map(Object::getClass)
                .distinct()
                .count()
            == blocks.stream().map(PageBlock::getProps).filter(PageBlock::singleUse).count();
  }
}
