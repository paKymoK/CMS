/**
 * Shared 40px circular prev/next arrow buttons for the drag carousels
 * (Services, Insights) — matches the handoff's spec exactly: 1px border,
 * dims to opacity .3 when disabled (no shake/rubber-band at the ends).
 */
export function CarouselArrows({
  onPrev,
  onNext,
  prevDisabled,
  nextDisabled,
  className = "",
}: {
  onPrev: () => void;
  onNext: () => void;
  prevDisabled?: boolean;
  nextDisabled?: boolean;
  className?: string;
}) {
  return (
    <div className={`flex items-center gap-3 ${className}`}>
      <button
        type="button"
        onClick={onPrev}
        disabled={prevDisabled}
        aria-label="Previous"
        className="flex h-10 w-10 items-center justify-center rounded-full border border-[#cfd2d6] text-lg text-[#3d4046] transition-[opacity,border-color,color] duration-200 hover:border-brand-primary hover:text-brand-primary disabled:opacity-30"
      >
        ‹
      </button>
      <button
        type="button"
        onClick={onNext}
        disabled={nextDisabled}
        aria-label="Next"
        className="flex h-10 w-10 items-center justify-center rounded-full border border-[#cfd2d6] text-lg text-[#3d4046] transition-[opacity,border-color,color] duration-200 hover:border-brand-primary hover:text-brand-primary disabled:opacity-30"
      >
        ›
      </button>
    </div>
  );
}
