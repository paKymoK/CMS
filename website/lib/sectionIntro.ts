import type { HomeContent, SectionIntro } from "@/content/home/types";

/** The CMS-managed eyebrow/heading/paragraph for one homepage section, by its anchor id. */
export function sectionIntro(content: HomeContent, section: string): SectionIntro | undefined {
  return content.sections?.section_intro?.find((i) => i.section === section);
}
