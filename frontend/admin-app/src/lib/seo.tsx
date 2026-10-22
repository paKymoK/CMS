import { Modal } from "antd";

/** ok = fine, warn = present but worth a look, missing = nothing set (a fallback is used instead). */
export type SeoLevel = "ok" | "warn" | "missing";

export interface SeoCheck {
  key: string;
  level: SeoLevel;
  label: string;
  detail: string;
}

/** The search-engine fields as they are in the form, and what the website falls back to when one is empty. */
export interface SeoValues {
  seoTitle: string;
  seoDescription: string;
  seoImage: string;
  fallbackTitle: string;
  fallbackDescription: string;
  fallbackImage: string;
  noindex: boolean;
}

export interface SeoCheckInput extends SeoValues {
  /** The page's own URL: omit for a page that lives at a fixed address (About). */
  slug?: { value: string; url: string };
}

export interface SnippetInput extends SeoValues {
  /** The site's address, e.g. en.cmcglobal.com. */
  host: string;
  /** The page's path on it, e.g. /insights/my-post. */
  path: string;
}

export type SnippetDevice = "desktop" | "mobile";

/** What one search result shows for a page on one kind of device. */
export interface Snippet {
  host: string;
  crumbs: string[];
  title: string;
  description: string;
  /** The share image (or the page's main image) to show as the result's thumbnail; empty when there is none. */
  image: string;
  titleCut: boolean;
  descriptionCut: boolean;
  /** The page asks not to be indexed, so it would not appear in results at all. */
  hidden: boolean;
}

/**
 * Where a result is cut off. Search engines cut by pixel width, not characters, so these are the
 * usual rule-of-thumb limits and the preview is approximate. A phone shows a longer title (two lines).
 */
const SNIPPET_LIMITS: Record<SnippetDevice, { title: number; description: number }> = {
  desktop: { title: 60, description: 160 },
  mobile: { title: 70, description: 130 },
};

/** Cuts `text` to `max` characters at a word boundary and marks the cut, or returns it as is. */
function cutAt(text: string, max: number): { text: string; cut: boolean } {
  if (text.length <= max) return { text, cut: false };
  const space = text.lastIndexOf(" ", max);
  return { text: `${text.slice(0, space > max / 2 ? space : max).trimEnd()}…`, cut: true };
}

/** The search result for a page: the title and description that would be shown, as cut for the device. */
export function seoSnippet(input: SnippetInput, device: SnippetDevice): Snippet {
  const limits = SNIPPET_LIMITS[device];
  const title = cutAt((input.seoTitle.trim() || input.fallbackTitle).replace(/\s+/g, " ").trim(), limits.title);
  const description = cutAt(
    (input.seoDescription.trim() || input.fallbackDescription).replace(/\s+/g, " ").trim(),
    limits.description,
  );
  return {
    host: input.host,
    crumbs: input.path.split("/").filter(Boolean),
    title: title.text,
    description: description.text,
    image: input.seoImage.trim() || input.fallbackImage.trim(),
    titleCut: title.cut,
    descriptionCut: description.cut,
    hidden: input.noindex,
  };
}

/** Search engines show about this many characters before cutting a title or description off. */
const TITLE_GUIDE = 60;
const DESCRIPTION_GUIDE = 160;

const clip = (text: string, max: number) => (text.length > max ? `${text.slice(0, max).trimEnd()}…` : text);

/** The editor's SEO checklist: what search engines and social sites will show, and what is missing. */
export function seoChecks(input: SeoCheckInput): SeoCheck[] {
  const checks: SeoCheck[] = [];

  if (input.slug) {
    checks.push(
      input.slug.value.trim()
        ? { key: "slug", level: "ok", label: "URL slug", detail: input.slug.url }
        : { key: "slug", level: "warn", label: "No URL slug yet", detail: "It is made from the title when you save." },
    );
  }

  const title = input.seoTitle.trim();
  if (!title) {
    checks.push({
      key: "title",
      level: "missing",
      label: "No meta title",
      detail: `Search engines will show: ${clip(input.fallbackTitle, 90)}`,
    });
  } else if (title.length > TITLE_GUIDE) {
    checks.push({
      key: "title",
      level: "warn",
      label: `Meta title is ${title.length} characters`,
      detail: `May be cut off after about ${TITLE_GUIDE}.`,
    });
  } else {
    checks.push({ key: "title", level: "ok", label: "Meta title", detail: `${title.length} / ${TITLE_GUIDE} characters` });
  }

  const description = input.seoDescription.trim();
  if (!description) {
    checks.push({
      key: "description",
      level: "missing",
      label: "No meta description",
      detail: input.fallbackDescription.trim()
        ? `Search engines will show: ${clip(input.fallbackDescription.trim(), 90)}`
        : "There is no text to fall back on: search engines will pick some from the page.",
    });
  } else if (description.length > DESCRIPTION_GUIDE) {
    checks.push({
      key: "description",
      level: "warn",
      label: `Meta description is ${description.length} characters`,
      detail: `May be cut off after about ${DESCRIPTION_GUIDE}.`,
    });
  } else {
    checks.push({
      key: "description",
      level: "ok",
      label: "Meta description",
      detail: `${description.length} / ${DESCRIPTION_GUIDE} characters`,
    });
  }

  if (input.seoImage.trim()) {
    checks.push({ key: "image", level: "ok", label: "Share image", detail: "A share image is set." });
  } else if (input.fallbackImage.trim()) {
    checks.push({ key: "image", level: "ok", label: "Share image", detail: "Using the page's main image." });
  } else {
    checks.push({
      key: "image",
      level: "missing",
      label: "No share image",
      detail: "The site's default image is used when this page is shared.",
    });
  }

  if (input.noindex) {
    checks.push({
      key: "noindex",
      level: "warn",
      label: "Hidden from search engines",
      detail: "This page will not appear in search results.",
    });
  }
  return checks;
}

/**
 * Runs `publish` straight away, or first asks once when SEO details are missing: a prompt, never a
 * block. Used only when a draft is being published, not on every update of a live page.
 */
export function publishUnlessSeoMissing(checks: SeoCheck[], publish: () => void) {
  const missing = checks.filter((c) => c.level === "missing");
  if (missing.length === 0) {
    publish();
    return;
  }
  Modal.confirm({
    title: "Some SEO details are missing",
    content: (
      <div>
        <p style={{ marginTop: 0 }}>The page still publishes fine: search engines and social sites will use what they find on it.</p>
        <ul style={{ paddingLeft: 18, margin: 0 }}>
          {missing.map((c) => (
            <li key={c.key}>
              <strong>{c.label}</strong>
              <div style={{ color: "#7b8aa5" }}>{c.detail}</div>
            </li>
          ))}
        </ul>
      </div>
    ),
    okText: "Publish anyway",
    cancelText: "Go back",
    onOk: publish,
  });
}
