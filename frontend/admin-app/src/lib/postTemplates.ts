/** Starting points for a new post. A template only pre-fills the editor; the author can change
 * everything afterwards, and the saved post keeps no link back to it. The built-ins live here;
 * ones authors save come from content-service (/v1/admin/post-templates). */
export interface PostTemplate {
  /** Built-ins use a string key, saved templates their numeric id. */
  id: string | number;
  name: string;
  description: string | null;
  layout: string;
  category: string | null;
  tags: string[] | null;
  body: string | null;
}

export interface SavedPostTemplate extends PostTemplate {
  id: number;
}

const BUTTON = '<p data-text-align="center"><a href="https://example.com" data-button="" rel="noopener noreferrer">Get in touch</a></p>';

export const BUILT_IN_TEMPLATES: PostTemplate[] = [
  { id: "blank", name: "Blank", description: "Start from an empty page.", layout: "default", category: null, tags: null, body: "" },
  {
    id: "article",
    name: "Article",
    description: "Intro, three sections and a closing call to action.",
    layout: "default",
    category: "insight",
    tags: null,
    body:
      "<p>Open with the one-sentence takeaway.</p>" +
      "<h2>The problem</h2><p>Describe what is changing and why it matters.</p>" +
      "<h2>What we found</h2><p>Share the evidence, with a table or an image if it helps.</p>" +
      "<h2>What to do next</h2><p>Give the reader a clear next step.</p>" +
      BUTTON,
  },
  {
    id: "announcement",
    name: "Announcement",
    description: "A highlighted headline, the details and a contact button.",
    layout: "focused",
    category: "news",
    tags: null,
    body:
      '<div data-callout=""><p><strong>The headline in one sentence.</strong></p></div>' +
      "<h2>The details</h2><p>Who, what, when and where.</p>" +
      "<h3>Why it matters</h3><p>What changes for the reader.</p>" +
      BUTTON,
  },
  {
    id: "faq",
    name: "FAQ",
    description: "A short intro followed by collapsible questions.",
    layout: "focused",
    category: "insight",
    tags: null,
    body:
      "<p>Answers to the questions we hear most.</p>" +
      "<details><summary>First question?</summary><p>Answer.</p></details>" +
      "<details><summary>Second question?</summary><p>Answer.</p></details>" +
      "<details><summary>Third question?</summary><p>Answer.</p></details>",
  },
  {
    id: "landing",
    name: "Landing page",
    description: "No header or sidebar: a headline, two columns and a button.",
    layout: "landing",
    category: "insight",
    tags: null,
    body:
      '<h2 data-text-align="center">A headline that says what this is</h2>' +
      '<p data-text-align="center">One supporting sentence.</p>' +
      '<div data-columns="2"><div data-column=""><h3>First point</h3><p>Explain it briefly.</p></div>' +
      '<div data-column=""><h3>Second point</h3><p>Explain it briefly.</p></div></div>' +
      BUTTON,
  },
];
