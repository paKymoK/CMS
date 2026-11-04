import { defaultSlots } from "./defaults";
import { AboutHero } from "./about/AboutHero";
import { Leadership } from "./about/Leadership";
import { MeetCmc } from "./about/MeetCmc";
import { StatementBand } from "./about/StatementBand";
import { Values } from "./about/Values";
import type { AboutPageData, PostArticleSlots } from "./types";

/**
 * The About page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped). Everything on it is in the
 * page's own data. A block that lacks its key text (the intro without a title, a band without text)
 * draws nothing, as before.
 */
export function AboutPage({ page, slots = defaultSlots }: { page: AboutPageData; slots?: PostArticleSlots }) {
  return (
    <>
      <AboutHero page={page} slots={slots} />
      {(page.blocks ?? []).map((block) => {
        if (block.hidden) return null;
        switch (block.type) {
          case "intro":
            return block.props.title ? <MeetCmc key={block.id} block={block.props} slots={slots} /> : null;
          case "vision":
            return block.props.eyebrow || block.props.title ? (
              <StatementBand
                key={block.id}
                variant="vision"
                eyebrow={block.props.eyebrow}
                title={block.props.title}
                image={block.props.image}
                slots={slots}
              />
            ) : null;
          case "mission":
            return block.props.eyebrow || block.props.body ? (
              <StatementBand
                key={block.id}
                variant="mission"
                eyebrow={block.props.eyebrow}
                body={block.props.body}
                image={block.props.image}
                slots={slots}
              />
            ) : null;
          case "leaders":
            return <Leadership key={block.id} block={block.props} slots={slots} />;
          case "values":
            return <Values key={block.id} block={block.props} slots={slots} />;
        }
      })}
    </>
  );
}
