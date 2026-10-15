import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { AboutHero } from "@/components/about/AboutHero";
import { MeetCmc } from "@/components/about/MeetCmc";
import { StatementBand } from "@/components/about/StatementBand";
import { Leadership } from "@/components/about/Leadership";
import { Values } from "@/components/about/Values";

const TITLE = "About Us | CMC Global";
const DESCRIPTION =
  "CMC Global unites passionate experts with advanced technology to scale your most ambitious digital vision — 33+ years of engineering excellence, 3000+ experts and offices across the world.";

export async function generateMetadata(props: PageProps<"/[locale]/about">): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({ locale, path: "about", title: TITLE, description: DESCRIPTION });
}

export default async function AboutPage(props: PageProps<"/[locale]/about">) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const { content, isPreview } = await loadContent();
  const s = content.sections;
  const hero = s?.about_hero?.[0];
  const intro = s?.about_intro?.[0];
  const vision = s?.about_statement?.find((x) => x.variant === "vision");
  const mission = s?.about_statement?.find((x) => x.variant === "mission");

  return (
    <>
      {isPreview && <DraftBanner />}
      {hero && <AboutHero hero={hero} />}
      {intro && <MeetCmc intro={intro} stats={content.stats} />}
      {vision && <StatementBand statement={vision} />}
      {mission && <StatementBand statement={mission} />}
      <Leadership
        leaders={s?.leader ?? []}
        intro={sectionIntro(content, "leaders")}
        ringImage={sectionIntro(content, "leaders")?.image}
      />
      <Values values={s?.about_value ?? []} intro={sectionIntro(content, "values")} />
    </>
  );
}
