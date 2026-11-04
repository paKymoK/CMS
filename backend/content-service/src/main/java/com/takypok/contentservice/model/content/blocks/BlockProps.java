package com.takypok.contentservice.model.content.blocks;

/**
 * The content of one block of a page. The block's {@code type} string picks the implementation (see
 * {@link ServiceBlock}); each page type lists the block types it allows, so a block that does not
 * belong on a page is rejected when the page is saved.
 */
public interface BlockProps {}
