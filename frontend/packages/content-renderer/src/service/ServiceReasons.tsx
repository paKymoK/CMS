import { defaultSlots } from "../defaults";
import type { PostArticleSlots, ReasonsBlockProps } from "../types";
import { ReasonsSlider } from "./ReasonsSlider";

/**
 * "Reasons to Partner With Us" (Figma "Content" → "Why choose us"). The slider itself is a client
 * component; the images are drawn here with the host's image slot and handed to it, since slots
 * (functions) cannot cross into a client component.
 */
export function ServiceReasons({ block, slots = defaultSlots }: { block: ReasonsBlockProps; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const reasons = block.items ?? [];
  if (reasons.length === 0) return null;
  return (
    <ReasonsSlider
      title={block.title ?? "Reasons to Partner With Us"}
      subtitle={block.subtitle}
      reasons={reasons.map((r) => ({
        title: r.title,
        body: r.body,
        main: r.image && <Image src={r.image} sizes="(min-width: 1280px) 36vw, 90vw" className="object-cover" />,
        side: r.sideImage && <Image src={r.sideImage} sizes="(min-width: 1280px) 21vw, 50vw" className="object-cover" />,
      }))}
    />
  );
}
