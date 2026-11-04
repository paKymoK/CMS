package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Success stories: case study slugs; the first one supplies the left-column figures. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoriesBlock implements BlockProps {
  private String heading;
  private List<String> caseStudies;
}
