"use client";

import { useEffect, useRef, useState } from "react";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

/**
 * A muted, looping, decorative video that fills a `relative` parent. The poster (the band's photo)
 * is painted by the caller underneath, so the section looks finished before — and without — any
 * video bytes. Nothing is fetched until the section nears the viewport, hls.js is only loaded
 * where the browser can't play HLS natively (everything but Safari), playback pauses off-screen,
 * and reduced-motion / data-saver visitors keep the still poster.
 */
export function BackdropVideo({ src, className = "" }: { src: string; className?: string }) {
  const ref = useRef<HTMLVideoElement>(null);
  const reducedMotion = usePrefersReducedMotion();
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const video = ref.current;
    const saveData = (navigator as Navigator & { connection?: { saveData?: boolean } }).connection
      ?.saveData;
    if (!video || reducedMotion || saveData) return;

    let hls: { destroy(): void } | undefined;
    let attached = false;
    let cancelled = false;

    const attach = async () => {
      if (attached) return;
      attached = true;
      if (video.canPlayType("application/vnd.apple.mpegurl")) {
        video.src = src;
        return;
      }
      const { default: Hls } = await import("hls.js");
      if (cancelled || !Hls.isSupported()) return;
      const instance = new Hls({ capLevelToPlayerSize: true, startLevel: 0 });
      instance.loadSource(src);
      instance.attachMedia(video);
      hls = instance;
    };

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          void attach().then(() => video.play().catch(() => {}));
        } else {
          video.pause();
        }
      },
      { rootMargin: "200px" },
    );
    observer.observe(video);

    return () => {
      cancelled = true;
      observer.disconnect();
      hls?.destroy();
    };
  }, [src, reducedMotion]);

  return (
    <video
      ref={ref}
      muted
      loop
      playsInline
      preload="none"
      aria-hidden
      tabIndex={-1}
      onPlaying={() => setReady(true)}
      className={`absolute inset-0 h-full w-full object-cover transition-opacity duration-500 ${
        ready ? "opacity-100" : "opacity-0"
      } ${className}`}
    />
  );
}
