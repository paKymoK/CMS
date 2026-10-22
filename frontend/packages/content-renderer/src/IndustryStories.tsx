import { defaultSlots } from "./defaults";
import { IndustryStoriesView } from "./IndustryStoriesView";
import type { IndustryStoryData, PostArticleSlots, SectionIntroData } from "./types";

/**
 * "Success Stories" (Figma "BFSI" → "Success Stories"). The card stack is a client component; the
 * photos are drawn here with the host's image slot and handed to it, since slots (functions)
 * cannot cross into a client component.
 */
export function IndustryStories({
  stories,
  intro,
  variant,
  slots = defaultSlots,
}: {
  stories: IndustryStoryData[];
  intro?: SectionIntroData;
  /** Extra class on the section root, for pages whose layout differs slightly (service pages). */
  variant?: string;
  slots?: PostArticleSlots;
}) {
  const { Image } = slots;
  return (
    <IndustryStoriesView
      stories={stories.map((s) => ({
        ...s,
        photo: s.image && <Image src={s.image} sizes="(min-width: 1280px) 28vw, 90vw" className="object-cover" />,
      }))}
      intro={intro}
      variant={variant}
    />
  );
}
