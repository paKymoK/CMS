import Image from "next/image";

/**
 * For logos of unknown/varying intrinsic aspect ratio rendered into a fixed
 * box. Using `fill` means no width/height HTML attributes are emitted, so
 * there's no prop-ratio-vs-CSS mismatch for Next.js to warn about, no matter
 * what the source file's real dimensions are.
 */
export function LogoImage({
  src,
  alt,
  width,
  height,
  sizes,
  className,
}: {
  src: string;
  alt: string;
  width: number;
  height: number;
  sizes?: string;
  className?: string;
}) {
  return (
    <div className={className} style={{ position: "relative", width, height }}>
      <Image src={src} alt={alt} fill sizes={sizes ?? `${width}px`} className="object-contain" />
    </div>
  );
}
