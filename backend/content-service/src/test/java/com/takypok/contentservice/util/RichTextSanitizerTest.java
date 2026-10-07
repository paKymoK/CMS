package com.takypok.contentservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;

class RichTextSanitizerTest {

  @Test
  void nullAndBlankPassThrough() {
    assertNull(RichTextSanitizer.sanitize(null));
    assertEquals("", RichTextSanitizer.sanitize(""));
  }

  @Test
  void stripsScriptsEventHandlersStylesAndJavascriptLinks() {
    String out =
        RichTextSanitizer.sanitize(
            "<p onclick=\"x()\" style=\"color:red\">a<script>alert(1)</script>"
                + "<a href=\"javascript:alert(1)\">bad</a></p>"
                + "<img src=\"/media-service/images/1.webp\" onerror=\"x()\">");
    assertFalse(out.contains("script"));
    assertFalse(out.contains("onclick"));
    assertFalse(out.contains("onerror"));
    assertFalse(out.contains("style"));
    assertFalse(out.contains("javascript:"));
    assertTrue(out.contains("<img src=\"/media-service/images/1.webp\">"));
  }

  @Test
  void keepsEditorOutputUnchanged() {
    String body =
        "<h2>Title</h2><p>a <strong>b</strong> <em>c</em> <u>d</u> <s>e</s> <code>f</code></p>"
            + "<ul><li><p>x</p></li></ul><blockquote><p>q</p></blockquote>"
            + "<figure data-align=\"left\"><img src=\"/media-service/images/1.webp\" alt=\"\">"
            + "<figcaption>cap</figcaption></figure>"
            + "<div data-callout=\"\"><p>n</p></div>"
            + "<table><tbody><tr><th colspan=\"2\"><p>h</p></th></tr></tbody></table><hr>";
    assertEquals(body, RichTextSanitizer.sanitize(body));
  }

  @Test
  void linksGetEnforcedRelAndOnlyAllowedSchemes() {
    String out =
        RichTextSanitizer.sanitize(
            "<a href=\"https://a.com\" target=\"_blank\">ok</a><a href=\"data:text/html,x\">d</a>"
                + "<a href=\"mailto:a@b.com\">m</a>");
    assertTrue(out.contains("rel=\"noopener noreferrer\""));
    assertTrue(out.contains("href=\"https://a.com\""));
    assertTrue(out.contains("href=\"mailto:a@b.com\""));
    assertFalse(out.contains("data:"));
  }

  @Test
  void keepsAllowlistedEmbedsAndDropsEverythingElse() {
    String good =
        "<div data-embed=\"video\"><iframe src=\"https://www.youtube-nocookie.com/embed/abc\" allowfullscreen=\"true\"></iframe></div>";
    assertTrue(RichTextSanitizer.sanitize(good).contains("youtube-nocookie.com/embed/abc"));
    assertTrue(
        RichTextSanitizer.sanitize(
                "<iframe src=\"https://docs.google.com/forms/d/e/1/viewform?embedded=true\"></iframe>")
            .contains("docs.google.com/forms/"));

    for (String bad :
        new String[] {
          "https://evil.com/x",
          "http://www.youtube-nocookie.com/embed/abc",
          "//evil.com",
          "https://docs.google.com/document/d/1",
          "https://www.youtube-nocookie.com.evil.com/embed/abc",
          "javascript:alert(1)"
        }) {
      assertFalse(
          RichTextSanitizer.sanitize("<iframe src=\"" + bad + "\"></iframe>").contains("<iframe"),
          bad);
    }
  }

  @Test
  void rejectsDangerousContentWithAReadableMessage() {
    for (String bad :
        new String[] {
          "<p>a</p><script>alert(1)</script>",
          "<img src=\"/x.webp\" onerror=\"x()\">",
          "<a href=\" JaVa\tScript:alert(1)\">x</a>",
          "<a href=\"data:text/html,x\">x</a>",
          "<iframe src=\"https://evil.com/x\"></iframe>",
          "<svg onload=\"x()\"></svg>",
          "<form action=\"https://evil.com\"></form>"
        }) {
      ApplicationException ex =
          assertThrows(
              ApplicationException.class, () -> RichTextSanitizer.sanitizeOrReject(bad), bad);
      assertTrue(ex.getMessage().contains("not allowed"), ex.getMessage());
    }
  }

  @Test
  void editorOutputIsNeverRejected() {
    // What Tiptap really emits: link rel/target, table inline styles + colgroup, embeds, figure.
    String editorBody =
        "<h2>T</h2><p><a target=\"_blank\" rel=\"noopener noreferrer nofollow\" href=\"https://x.com\">l</a></p>"
            + "<table style=\"min-width: 25px;\"><colgroup><col style=\"min-width: 25px;\"></colgroup>"
            + "<tbody><tr><th><p>a</p></th></tr></tbody></table>"
            + "<div data-embed=\"video\"><iframe src=\"https://www.youtube-nocookie.com/embed/abc\" title=\"Embedded video\" loading=\"lazy\" allowfullscreen=\"true\" referrerpolicy=\"strict-origin-when-cross-origin\"></iframe></div>"
            + "<figure data-align=\"full\"><img src=\"/media-service/images/1.webp\" alt=\"\"><figcaption>c</figcaption></figure>";
    assertTrue(RichTextSanitizer.findViolations(editorBody).isEmpty());
    assertFalse(RichTextSanitizer.sanitizeOrReject(editorBody).contains("style="));
  }

  @Test
  void keepsLayoutBlocksAlignmentColorAndButtons() {
    String body =
        "<h4 data-text-align=\"center\">h</h4><p data-text-align=\"right\">a <span data-color=\"blue\">b</span></p>"
            + "<p><a href=\"https://a.com\" data-button=\"\" rel=\"noopener noreferrer\">Go</a></p>"
            + "<div data-columns=\"2\"><div data-column=\"\"><p>l</p></div><div data-column=\"\"><p>r</p></div></div>"
            + "<details><summary>Q</summary><p>A</p></details>";
    assertEquals(body, RichTextSanitizer.sanitize(body));
  }

  @Test
  void stripsOpenFromDetailsAndInlineStyleFromSpans() {
    String out =
        RichTextSanitizer.sanitize(
            "<details open=\"\"><summary>Q</summary><p>A</p></details><span style=\"color:red\">x</span>");
    assertFalse(out.contains("open"));
    assertFalse(out.contains("style"));
  }

  @Test
  void embedHostsComeFromTheSharedResource() {
    String ok =
        "<div data-embed=\"video\"><iframe src=\"https://www.loom.com/embed/abc123\"></iframe></div>";
    assertTrue(RichTextSanitizer.sanitize(ok).contains("loom.com/embed/abc123"));
    String wrongPath =
        "<div data-embed=\"video\"><iframe src=\"https://www.loom.com/share/abc123\"></iframe></div>";
    assertFalse(RichTextSanitizer.sanitize(wrongPath).contains("iframe"));
  }
}
