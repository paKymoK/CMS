import type { PostArticleSlots } from "./types";

/** Plain-HTML slots, for hosts without a router or an image optimiser (the admin preview). */
export const defaultSlots: PostArticleSlots = {
  Link: ({ href, className, children }) => (
    <a href={href} className={className}>
      {children}
    </a>
  ),
  Image: ({ src, className }) => (
    <img src={src} alt="" className={`absolute inset-0 h-full w-full ${className ?? ""}`} />
  ),
};
