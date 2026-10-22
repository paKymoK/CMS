import { defaultSlots } from "../defaults";
import type { IndustryPageData, PostArticleSlots } from "../types";
import { SolutionsCarousel } from "./SolutionsCarousel";

/**
 * The solutions carousel. The carousel itself is a client component; the photos are drawn here with
 * the host's image slot and handed to it, since slots (functions) cannot cross into a client component.
 */
export function IndustrySolutions({ page, slots = defaultSlots }: { page: IndustryPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const groups = (page.solutionGroups ?? [])
    .filter((g) => (g.slides ?? []).length > 0)
    .map((g) => ({
      name: g.name,
      slides: (g.slides ?? []).map((s) => ({
        ...s,
        photo: s.image && <Image src={s.image} sizes="(min-width: 1280px) 45vw, 100vw" className="object-cover" />,
      })),
    }));
  if (groups.length === 0) return null;
  return (
    <SolutionsCarousel
      groups={groups}
      intro={{ eyebrow: page.solutionsEyebrow, title: page.solutionsTitle ?? "Our Solutions", body: page.solutionsBody }}
    />
  );
}
