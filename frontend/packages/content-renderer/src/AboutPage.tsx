import { defaultSlots } from "./defaults";
import { AboutHero } from "./about/AboutHero";
import { Leadership } from "./about/Leadership";
import { MeetCmc } from "./about/MeetCmc";
import { StatementBand } from "./about/StatementBand";
import { Values } from "./about/Values";
import type { AboutPageData, PostArticleSlots } from "./types";

/**
 * The About page, drawn the same way for the live site and for the admin preview. Everything on it is
 * in the page's own data; its switches (`hiddenSections`) decide which sections show.
 */
export function AboutPage({ page, slots = defaultSlots }: { page: AboutPageData; slots?: PostArticleSlots }) {
  const hidden = new Set(page.hiddenSections ?? []);
  return (
    <>
      <AboutHero page={page} slots={slots} />
      {!hidden.has("intro") && page.introTitle && <MeetCmc page={page} slots={slots} />}
      {!hidden.has("vision") && (page.visionEyebrow || page.visionTitle) && (
        <StatementBand
          variant="vision"
          eyebrow={page.visionEyebrow}
          title={page.visionTitle}
          image={page.visionImage}
          slots={slots}
        />
      )}
      {!hidden.has("mission") && (page.missionEyebrow || page.missionBody) && (
        <StatementBand
          variant="mission"
          eyebrow={page.missionEyebrow}
          body={page.missionBody}
          image={page.missionImage}
          slots={slots}
        />
      )}
      {!hidden.has("leaders") && <Leadership page={page} slots={slots} />}
      {!hidden.has("values") && <Values page={page} slots={slots} />}
    </>
  );
}
