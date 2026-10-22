import Image, { type ImageProps } from "next/image";

/**
 * The one way to render a content/media image that fills a sized parent
 * (the parent must be `relative` with an explicit size or aspect ratio).
 * `sizes` is REQUIRED — without it Next.js assumes 100vw and ships an
 * oversized srcset (and logs a console warning). Pass `priority` for
 * above-the-fold/LCP images. For logos in a fixed box use `LogoImage`.
 */
export function CoverImage({
  alt,
  sizes,
  className = "object-cover",
  ...props
}: Omit<ImageProps, "fill" | "width" | "height" | "sizes"> & { sizes: string }) {
  return <Image {...props} alt={alt} fill sizes={sizes} className={className} />;
}
