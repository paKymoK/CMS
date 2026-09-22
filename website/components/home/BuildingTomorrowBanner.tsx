import { Reveal } from "@/components/ui/Reveal";

export function BuildingTomorrowBanner() {
  return (
    <section className="px-6 pb-[clamp(56px,7vw,96px)]">
      <Reveal travelPx={0} className="mx-auto max-w-[1072px]">
        <div
          className="px-9 py-10"
          style={{ background: "linear-gradient(90deg, #03091a 40%, #0b3c78)" }}
        >
          <h2 className="text-[clamp(20px,2.6vw,28px)] leading-[1.2] font-bold tracking-[-0.015em] text-white">
            Building Tomorrow
            <br />
            Together
          </h2>
        </div>
      </Reveal>
    </section>
  );
}
