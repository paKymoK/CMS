package com.takypok.contentservice.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A saved starting point for pages: either a whole page layout (kind PAGE: an ordered list of
 * blocks, used to create a page) or one block (kind BLOCK, a list of one, inserted into a page).
 * Blocks are copied out of it; nothing links a page back to the item it started from.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table("block_library")
public class BlockLibraryItem extends IdEntity {
  public static final String PAGE = "PAGE";
  public static final String BLOCK = "BLOCK";

  private Long siteId;
  private String kind;
  private String name;
  private String description;

  /**
   * [{ "type": "process", "hidden": false, "props": {...} }], without ids (the page gives each copy
   * its own).
   */
  private JsonNode blocks;
}
