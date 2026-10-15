import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import { Link } from "@/i18n/navigation";
import type { PostSummary } from "@/lib/cms/types";

/**
 * One article card on the Insights page (Figma "Insight" → "Capabilities" cards): photo on top, a
 * white panel with the headline and the topic pills, all inside a thin grey outline.
 */
export function InsightCard({ post, sizes }: { post: PostSummary; sizes?: string }) {
  return (
    <Link href={`/insights/${post.slug}`} className="ins-card">
      <div className="ins-card-img">
        {post.image ? (
          <CoverImage
            src={post.image}
            alt=""
            sizes={sizes ?? "(min-width: 1280px) 480px, (min-width: 640px) 50vw, 100vw"}
            className="object-cover"
          />
        ) : (
          <Placeholder tone="navy" label="Photo" fill />
        )}
      </div>
      <div className="ins-card-body">
        <h3 className="ins-card-title">{post.title}</h3>
        {post.tags.length > 0 && (
          <ul className="ins-card-tags">
            {post.tags.map((tag) => (
              <li key={tag}>{tag}</li>
            ))}
          </ul>
        )}
      </div>
    </Link>
  );
}
