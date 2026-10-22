import type { PostArticleSlots } from "@cms/content-renderer";
import { CoverImage } from "@/components/ui/CoverImage";
import { Link } from "@/i18n/navigation";

/** The site's router link and optimised image, handed to the shared post renderer. */
export const siteSlots: PostArticleSlots = {
  Link: ({ href, className, children }) => (
    <Link href={href} className={className}>
      {children}
    </Link>
  ),
  Image: ({ src, sizes, priority, className }) => (
    <CoverImage src={src} alt="" sizes={sizes} priority={priority} className={className} />
  ),
};
