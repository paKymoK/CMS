"use client";

// Presentational only — matches ContactCta.tsx's precedent: no backend to
// submit to yet, so the form doesn't post anywhere.
const FIELDS = [
  { name: "fullName", type: "text", placeholder: "Full name*" },
  { name: "email", type: "email", placeholder: "Email*" },
  { name: "phone", type: "tel", placeholder: "Phone number*" },
  { name: "company", type: "text", placeholder: "Company name*" },
  { name: "jobTitle", type: "text", placeholder: "Job title*" },
  { name: "industry", type: "text", placeholder: "Industry*" },
] as const;

export function ContactForm() {
  return (
    <section
      id="contact"
      className="py-[clamp(56px,7vw,96px)]"
      style={{ background: "linear-gradient(#f2f7fd,#e7f0fa)" }}
    >
      <div className="px-6">
        <h2 className="mx-auto mb-8 max-w-[560px] text-center text-[clamp(18px,2.4vw,24px)] font-bold tracking-[0.02em] text-brand-primary uppercase">
          Maximize your ROI in the cloud
          <br />
          Let&apos;s chat!
        </h2>

        <form
          onSubmit={(e) => e.preventDefault()}
          className="mx-auto grid max-w-[620px] grid-cols-[repeat(auto-fit,minmax(200px,1fr))] gap-3.5 border border-[#e0e4e9] bg-white p-[30px]"
        >
          {FIELDS.map((field) => (
            <input
              key={field.name}
              type={field.type}
              name={field.name}
              placeholder={field.placeholder}
              className="min-w-0 border border-[#dfe3e8] px-3.5 py-3.5 text-sm outline-none transition-colors duration-150 focus:border-brand-primary"
            />
          ))}
          <textarea
            name="message"
            rows={3}
            placeholder="Message*"
            className="col-span-full min-w-0 resize-y border border-[#dfe3e8] px-3.5 py-3.5 text-sm outline-none transition-colors duration-150 focus:border-brand-primary"
          />
          <button
            type="submit"
            className="font-mono-wave col-span-full min-h-11 bg-brand-primary py-3.5 text-xs tracking-[0.1em] text-white transition-colors hover:bg-brand-primary-dark"
          >
            SEND MESSAGE
          </button>
        </form>
      </div>
    </section>
  );
}
