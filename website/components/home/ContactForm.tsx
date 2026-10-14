"use client";

import { useState } from "react";
import type { SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "contact",
  title: "Maximize Your ROI in the Cloud\nLet's Chat!",
};

const inputBase = "ctc-input";

/**
 * "Let's Chat" (Figma "contact"): gradient heading and the contact form on a pale-blue wash, with
 * the round back-to-top button at the bottom right. The form validates in the browser but doesn't
 * post anywhere yet — there is no contact endpoint in the backend — so submitting only shows a
 * confirmation.
 */
export function ContactForm({ intro = FALLBACK_INTRO }: { intro?: SectionIntro }) {
  const [sent, setSent] = useState(false);

  return (
    <section id="contact" aria-labelledby="contact-title" className="design-canvas">
      <div className="ctc">
        <h2 id="contact-title" className="sec-title ctc-title">
          {intro.title}
        </h2>

        <form
          className="ctc-form"
          onSubmit={(e) => {
            e.preventDefault();
            setSent(true);
          }}
        >
          <label className="ctc-field ctc-field--wide">
            <span className="sr-only">Purpose</span>
            <input
              className={inputBase}
              name="purpose"
              type="text"
              placeholder="Purpose*"
              required
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Full name</span>
            <input
              className={inputBase}
              name="fullName"
              type="text"
              placeholder="Full Name*"
              required
              autoComplete="name"
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Email</span>
            <input
              className={inputBase}
              name="email"
              type="email"
              placeholder="Email*"
              required
              autoComplete="email"
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Phone number</span>
            <input
              className={inputBase}
              name="phone"
              type="tel"
              placeholder="Phone Number*"
              required
              autoComplete="tel"
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Company name</span>
            <input
              className={inputBase}
              name="company"
              type="text"
              placeholder="Company Name*"
              required
              autoComplete="organization"
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Job title</span>
            <input
              className={inputBase}
              name="jobTitle"
              type="text"
              placeholder="Job Title*"
              required
              autoComplete="organization-title"
            />
          </label>
          <label className="ctc-field">
            <span className="sr-only">Industry</span>
            <input className={inputBase} name="industry" type="text" placeholder="Industry" />
          </label>
          <label className="ctc-field ctc-field--wide">
            <span className="sr-only">Message</span>
            <textarea
              className={inputBase}
              name="message"
              rows={3}
              placeholder="Message*"
              required
            />
          </label>
          <label className="ctc-field ctc-field--wide">
            <span className="sr-only">Book a meeting</span>
            <input
              className={inputBase}
              name="meetingDate"
              type="text"
              placeholder="Book Meeting*"
              required
              onFocus={(e) => (e.currentTarget.type = "date")}
              onBlur={(e) => {
                if (!e.currentTarget.value) e.currentTarget.type = "text";
              }}
            />
          </label>
          <label className="ctc-field ctc-field--wide">
            <span className="sr-only">Time (GMT +7)</span>
            <input
              className={inputBase}
              name="meetingTime"
              type="text"
              placeholder="Time (GMT +7)*"
              required
              onFocus={(e) => (e.currentTarget.type = "time")}
              onBlur={(e) => {
                if (!e.currentTarget.value) e.currentTarget.type = "text";
              }}
            />
          </label>
          <button type="submit" className="ctc-submit">
            Submit
          </button>
          {sent && (
            <p className="ctc-note" role="status">
              Thank you — your message is ready to send. (This form isn&apos;t connected to a
              backend yet.)
            </p>
          )}
        </form>

        <button
          type="button"
          className="ctc-top"
          aria-label="Back to top"
          onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
        >
          <svg
            viewBox="0 0 24 24"
            className="h-1/2 w-1/2 fill-none stroke-current stroke-2"
            aria-hidden
          >
            <path d="M12 19V5M5 12l7-7 7 7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </button>
      </div>
    </section>
  );
}
