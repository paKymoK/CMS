import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import { Tooltip } from "@/components/ui/Tooltip";
import { Link } from "@/i18n/navigation";
import type { PostSummary } from "@/lib/cms/types";

const sentence = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);

/**
 * One card on the Perspectives listing (wireframe card): thumbnail, a content-type pill, bold title
 * and excerpt, then the date on the left and a "Discover" link on the right, on the same white card
 * with a light-grey edge as the Case Studies cards. A clamped title shows in full in a tooltip.
 */
export function PerspectiveCard({ post }: { post: PostSummary }) {
  return (
    <Tooltip
      as="li"
      className="psp-cell"
      content={post.title}
      truncatedSelector=".psp-card-title"
    >
      <Link
        href={`/insights/${post.slug}`}
        className="psp-card"
        aria-label={post.title}
      >
        <div className="psp-card-img">
          {post.image ? (
            <CoverImage
              src={post.image}
              alt=""
              sizes="(min-width: 1280px) 22vw, (min-width: 640px) 46vw, 92vw"
              className="object-cover"
            />
          ) : (
            <Placeholder tone="navy" label="Perspective" fill />
          )}
        </div>
        {post.category && (
          <span className="psp-pill">{sentence(post.category)}</span>
        )}
        <h2 className="psp-card-title">{post.title}</h2>
        {post.excerpt && <p className="psp-card-sum">{post.excerpt}</p>}
        <div className="psp-card-foot">
          <span className="psp-card-date">
            <svg viewBox="0 0 28 28" aria-hidden>
              <rect
                x="5"
                y="6.5"
                width="18"
                height="16"
                rx="2"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              />
              <path
                d="M5 11.5h18M10 4v5M18 4v5"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
              />
            </svg>
            {post.date}
          </span>
          <span className="psp-card-more">
            Discover
            <svg viewBox="0 0 28 28" aria-hidden>
              <circle cx="14" cy="14" r="11.5" fill="#003f79" />
              <path
                d="M8.8 14h10.4M15.6 9.6l4.4 4.4-4.4 4.4"
                fill="none"
                stroke="#fff"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </span>
        </div>
      </Link>
    </Tooltip>
  );
}
