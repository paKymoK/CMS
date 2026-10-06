package com.takypok.contentservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

/**
 * Allowlist sanitizer for rich-text bodies (posts, case studies), applied on write so stored HTML
 * is already clean no matter who called the API. Mirrors what the admin editor's schema can produce
 * and what the website's render-time sanitizer (website/lib/richText.ts) allows — keep the three in
 * sync. Iframes are only kept for the embed providers the editor offers.
 */
@Slf4j
public final class RichTextSanitizer {

  /** hostname -> required path prefix ("" = any). Mirrors admin-app/src/lib/embeds.ts. */
  private static final Map<String, String> EMBED_HOSTS =
      Map.of(
          "www.youtube-nocookie.com", "",
          "player.vimeo.com", "",
          "docs.google.com", "/forms/",
          "tally.so", "",
          "form.typeform.com", "",
          "form.jotform.com", "",
          "forms.office.com", "");

  private static final Safelist SAFELIST =
      new Safelist()
          .addTags(
              "p",
              "br",
              "h2",
              "h3",
              "ul",
              "ol",
              "li",
              "blockquote",
              "pre",
              "code",
              "hr",
              "strong",
              "em",
              "u",
              "s",
              "a",
              "figure",
              "figcaption",
              "img",
              "table",
              "thead",
              "tbody",
              "tr",
              "th",
              "td",
              "div",
              "iframe")
          .addAttributes("a", "href", "target")
          .addAttributes("img", "src", "alt")
          .addAttributes("figure", "data-align")
          .addAttributes("div", "data-callout", "data-embed")
          .addAttributes("th", "colspan", "rowspan")
          .addAttributes("td", "colspan", "rowspan")
          .addAttributes("iframe", "src", "title", "loading", "allowfullscreen", "referrerpolicy")
          .addProtocols("a", "href", "http", "https", "mailto", "tel")
          .addProtocols("img", "src", "http", "https")
          .addProtocols("iframe", "src", "https")
          .addEnforcedAttribute("a", "rel", "noopener noreferrer")
          // Media is stored as origin-less /media-service/... paths and links may be site paths.
          .preserveRelativeLinks(true);

  /** jsoup only keeps a relative URL if it resolves against the base to an allowed protocol. */
  private static final String BASE_URI = "https://base.invalid/";

  private static final Set<String> DANGEROUS_TAGS =
      Set.of(
          "script",
          "style",
          "object",
          "embed",
          "applet",
          "form",
          "link",
          "meta",
          "base",
          "svg",
          "math",
          "template",
          "frame",
          "frameset",
          "noscript");

  private static final Set<String> URL_ATTRS =
      Set.of("href", "src", "action", "formaction", "xlink:href", "data", "poster");

  private RichTextSanitizer() {}

  /**
   * Cleans the body, but first rejects it outright if it contains something only an attacker (or a
   * client bypassing the editor) would send: script-like tags, event handlers, javascript:/data:
   * URLs, or an iframe from a non-allowlisted source. The editor can never produce these, so a hit
   * is a real signal — benign leftovers it does emit (e.g. table inline styles) are cleaned
   * silently instead.
   *
   * @throws ApplicationException (HTTP 400, shown to the author) listing what was found
   */
  public static String sanitizeOrReject(String html) {
    if (html == null || html.isBlank()) return html;
    Set<String> violations = findViolations(html);
    if (!violations.isEmpty()) {
      log.warn("Rejected rich-text body with disallowed content: {}", violations);
      throw new ApplicationException(
          Message.Application.ERROR,
          "Body contains content that is not allowed: " + String.join("; ", violations));
    }
    return sanitize(html);
  }

  /**
   * Human-readable descriptions of dangerous / not-allowed content in {@code html} (no raw values).
   */
  public static Set<String> findViolations(String html) {
    Set<String> out = new LinkedHashSet<>();
    if (html == null || html.isBlank()) return out;
    Document doc = Jsoup.parseBodyFragment(html, BASE_URI);
    for (Element el : doc.body().getAllElements()) {
      String tag = el.normalName();
      if (DANGEROUS_TAGS.contains(tag)) out.add("<" + tag + "> element");
      if ("iframe".equals(tag) && !isAllowedEmbed(el.attr("src"))) {
        out.add("iframe from a source that is not an allowed embed provider");
      }
      for (org.jsoup.nodes.Attribute attr : el.attributes()) {
        String name = attr.getKey().toLowerCase(Locale.ROOT);
        if (name.startsWith("on")) out.add("event handler attribute (" + name + ")");
        if (name.equals("srcdoc")) out.add("srcdoc attribute");
        if (URL_ATTRS.contains(name)) {
          String v = attr.getValue().replaceAll("[\\s\\p{Cntrl}]", "").toLowerCase(Locale.ROOT);
          if (v.startsWith("javascript:") || v.startsWith("vbscript:") || v.startsWith("data:")) {
            out.add("unsafe URL scheme in " + name);
          }
        }
      }
    }
    return out;
  }

  /** Returns sanitized HTML; null/blank input is returned unchanged. */
  public static String sanitize(String html) {
    if (html == null || html.isBlank()) return html;
    String cleaned =
        Jsoup.clean(html, BASE_URI, SAFELIST, new Document.OutputSettings().prettyPrint(false));
    Document doc = Jsoup.parseBodyFragment(cleaned);
    doc.outputSettings().prettyPrint(false);
    for (Element iframe : doc.select("iframe")) {
      if (!isAllowedEmbed(iframe.attr("src"))) iframe.remove();
    }
    return doc.body().html();
  }

  private static boolean isAllowedEmbed(String src) {
    try {
      URI uri = URI.create(src);
      if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) return false;
      String prefix = EMBED_HOSTS.get(uri.getHost().toLowerCase());
      if (prefix == null) return false;
      String path = uri.getPath() == null ? "" : uri.getPath();
      return path.startsWith(prefix);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
