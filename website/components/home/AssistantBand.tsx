"use client";

import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { useLocale } from "next-intl";
import { NAV_SECTIONS } from "@/content/nav";
import { useSectionSpy } from "@/lib/useSectionSpy";
import { SectionDock } from "./SectionDock";
import type { AssistantBand as AssistantBandContent } from "@/content/home/types";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

const FALLBACK_REPLY =
  "Sorry, I'm having trouble reaching our knowledge base right now — please try again in a moment, or reach out to our team directly.";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** Artboard (1920px) pixels → a length that scales with the band's width. */
const u = (n: number) => `calc(${n} * var(--u))`;

type Message = { role: "user" | "bot"; text: string };
type AssistantTurn = { role: "USER" | "ASSISTANT"; content: string };

/**
 * chat-service's PublicAssistantController is stateless — it doesn't persist a session, so the
 * widget resends its own running transcript on every call. Same gateway-routed base as
 * lib/cms/homeContent.ts (never straight to chat-service's own port).
 */
async function askAssistant(
  site: string,
  question: string,
  history: AssistantTurn[],
): Promise<{ answer: string; sources: string[] }> {
  const res = await fetch(`${CMS_BASE}/chat-service/v1/assistant/ask`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ site, question, history }),
  });
  if (!res.ok) {
    throw new Error(`Assistant request failed: ${res.status} ${res.statusText}`);
  }
  return res.json();
}

/** The design's "lower third" menu: in-page anchors with a scrollspy-driven active state. */
function LowerThirdNav({
  active,
  navRef,
}: {
  active: number;
  navRef: React.RefObject<HTMLElement | null>;
}) {
  return (
    <nav
      ref={navRef}
      aria-label="Page sections"
      className="asst-nav mx-auto flex w-full overflow-x-auto [scrollbar-width:none] lg:justify-center [&::-webkit-scrollbar]:hidden"
    >
      <ul className="mx-auto flex w-fit items-center" style={{ gap: `max(24px, ${u(43)})` }}>
        {NAV_SECTIONS.map((s, i) => (
          <li key={s.id} className="flex-none">
            <a
              href={`#${s.id}`}
              aria-current={i === active ? "true" : undefined}
              className="relative block font-medium whitespace-nowrap transition-colors hover:text-[#00b2ff]"
              style={{
                fontSize: `max(15px, ${u(20)})`,
                lineHeight: 1.31,
                color: i === active ? "#00b2ff" : "#ffffff",
              }}
            >
              {s.label}
              {i === active && (
                <span
                  aria-hidden
                  className="absolute left-1/2 -translate-x-1/2 rounded-sm bg-[#00b2ff]"
                  style={{ top: "100%", width: 38, height: 2 }}
                />
              )}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}

/**
 * The AI-assistant band under the hero (Figma "AI Chatbot"): a frosted card over a backdrop image
 * with the headline, an input bar showing the greeting, and the lower-third section menu. Starting
 * a conversation (typing, or tapping a suggestion) opens the chat log and suggestion chips — the
 * design's expanded "chatbot" state. Copy and imagery come from the CMS ("assistant_band").
 */
export function AssistantBand({ band }: { band: AssistantBandContent }) {
  const reducedMotion = usePrefersReducedMotion();
  const locale = useLocale();
  const logRef = useRef<HTMLDivElement>(null);
  // chat-service is stateless: the running transcript is sent back on every call, and only
  // successful replies enter it (a client-side fallback never does).
  const historyRef = useRef<AssistantTurn[]>([]);
  const active = useSectionSpy();
  const navRef = useRef<HTMLElement>(null);
  // The docked copy of the section menu shows once the in-card one has scrolled off the top, and
  // hides again at the bottom of the page (Recognition onwards), where it would cover content.
  const [navScrolledPast, setNavScrolledPast] = useState(false);
  const [atPageEnd, setAtPageEnd] = useState(false);
  const dockShown = navScrolledPast && !atPageEnd;

  useEffect(() => {
    const el = navRef.current;
    if (!el) return;
    const observer = new IntersectionObserver(([entry]) =>
      setNavScrolledPast(!entry.isIntersecting && entry.boundingClientRect.top < 0),
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    const end = document.getElementById("recognition");
    if (!end) return;
    // From the moment the Recognition section's top edge enters the screen, until the page ends.
    const observer = new IntersectionObserver(([entry]) =>
      setAtPageEnd(entry.isIntersecting || entry.boundingClientRect.top < 0),
    );
    observer.observe(end);
    return () => observer.disconnect();
  }, []);

  const [started, setStarted] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [busy, setBusy] = useState(false);
  const [slow, setSlow] = useState(false);
  const [streamingText, setStreamingText] = useState<string | null>(null);
  const [input, setInput] = useState("");

  useEffect(() => {
    logRef.current?.scrollTo({ top: logRef.current.scrollHeight });
  }, [messages, streamingText, started]);

  async function sendMessage(text: string) {
    const trimmed = text.trim();
    if (!trimmed || busy) return;
    setStarted(true);
    setMessages((prev) => [...prev, { role: "user", text: trimmed }]);
    setInput("");
    setBusy(true);
    setStreamingText(null);

    const slowTimer = setTimeout(() => setSlow(true), 1500);

    let reply: string;
    let ok = true;
    try {
      reply = (await askAssistant(locale, trimmed, historyRef.current)).answer;
    } catch (err) {
      console.error("Assistant request failed", err);
      reply = FALLBACK_REPLY;
      ok = false;
    }

    if (ok) {
      historyRef.current = [
        ...historyRef.current,
        { role: "USER", content: trimmed },
        { role: "ASSISTANT", content: reply },
      ];
    }

    clearTimeout(slowTimer);
    setSlow(false);

    const words = reply.split(" ");
    let shown = 0;
    setStreamingText("");
    const interval = setInterval(
      () => {
        shown = Math.min(words.length, shown + 3);
        setStreamingText(words.slice(0, shown).join(" "));
        if (shown >= words.length) {
          clearInterval(interval);
          setMessages((prev) => [...prev, { role: "bot", text: reply }]);
          setStreamingText(null);
          setBusy(false);
        }
      },
      reducedMotion ? 0 : 90,
    );
  }

  const bubble =
    "max-w-[88%] rounded-[10px] px-4 py-2.5 text-[15px] leading-[1.5] whitespace-pre-wrap";

  return (
    <>
      {/* The outer element is the size container; the inner one carries the artboard-scaled height
        (an element can't resolve container units against itself). */}
      <section aria-label="AI assistant" className="design-canvas">
        <div className={`asst-band${started ? " asst-band--open" : ""}`}>
          {/* The backdrop is drawn 2757px wide on the artboard and cropped by the band. */}
          <div
            aria-hidden
            className="absolute"
            style={{
              left: u(-43),
              top: u(-448),
              width: u(2757),
              height: u(1530),
              minWidth: "100%",
              minHeight: "100%",
            }}
          >
            {band.backdropImage && (
              <Image src={band.backdropImage} alt="" fill sizes="100vw" className="object-cover" />
            )}
          </div>

          <div className="asst-card">
            <h2 className="asst-title">{band.title.replace(/ (To Know)/, "\n$1")}</h2>

            {started && (
              <div
                ref={logRef}
                className="asst-bar-wrap mt-6 flex max-h-[320px] flex-col gap-3 overflow-y-auto rounded-[13px] px-5 py-4"
                style={{
                  background: "rgba(8,33,88,.55)",
                  border: "1px solid rgba(255,255,255,.3)",
                }}
              >
                {band.welcome && (
                  <p className={`${bubble} bg-white/10 text-white`}>{band.welcome}</p>
                )}
                {messages.map((m, i) => (
                  <p
                    key={i}
                    className={`${bubble} ${
                      m.role === "user"
                        ? "ml-auto bg-[#0b63c5] text-white"
                        : "bg-white/10 text-white"
                    }`}
                  >
                    {m.text}
                  </p>
                ))}
                {busy && streamingText === null && (
                  <div className="flex gap-1.5" aria-label="Assistant is typing">
                    {[0, 1, 2].map((i) => (
                      <span
                        key={i}
                        className="dot-pulse h-1.5 w-1.5 rounded-full bg-white/70"
                        style={{ animationDelay: `${i * 0.16}s` }}
                      />
                    ))}
                  </div>
                )}
                {busy && slow && streamingText === null && (
                  <div className="flex flex-col gap-1.5" aria-hidden>
                    <span className="shimmer h-2.5 w-4/5 rounded bg-white/10" />
                    <span className="shimmer h-2.5 w-3/5 rounded bg-white/10" />
                  </div>
                )}
                {streamingText !== null && (
                  <p className={`${bubble} bg-white/10 text-white`}>{streamingText}</p>
                )}
              </div>
            )}

            {/* Input bar — the design's pale glass pill: orb, greeting text, send arrow. */}
            <form
              className="asst-bar"
              style={started ? { marginTop: 16 } : undefined}
              onSubmit={(e) => {
                e.preventDefault();
                sendMessage(input);
              }}
            >
              <span aria-hidden className="asst-bar-orb">
                {band.orbImage && (
                  <Image src={band.orbImage} alt="" fill sizes="64px" className="object-contain" />
                )}
              </span>
              <textarea
                rows={1}
                value={input}
                onChange={(e) => setInput(e.target.value.replace(/\n/g, " "))}
                onFocus={() => setStarted(true)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    e.preventDefault();
                    sendMessage(input);
                  }
                }}
                placeholder={band.greeting}
                aria-label="Ask CMC Global a question"
                className="asst-bar-input"
              />
              <button type="submit" disabled={busy} aria-label="Send" className="asst-bar-send">
                <svg
                  viewBox="0 0 24 24"
                  className="h-full w-full fill-none stroke-current stroke-2"
                  aria-hidden
                >
                  <path d="M21 3 3 10.5l7 2.5 2.5 7L21 3Z" strokeLinejoin="round" />
                  <path d="m10 13 4-4" strokeLinecap="round" />
                </svg>
              </button>
            </form>

            {started && band.suggestions && band.suggestions.length > 0 && (
              <div className="asst-bar-wrap mt-4 flex flex-wrap justify-end gap-3">
                {band.suggestions.map((chip) => (
                  <button
                    key={chip}
                    type="button"
                    onClick={() => sendMessage(chip)}
                    className="rounded-[12px] bg-white px-5 py-2.5 text-[15px] text-black transition-colors hover:bg-[#e8f1fb]"
                  >
                    {chip}
                  </button>
                ))}
              </div>
            )}

            <LowerThirdNav active={active} navRef={navRef} />
          </div>
        </div>
      </section>
      {/* Outside the container: layout containment would otherwise anchor `fixed` to the band. */}
      <SectionDock active={active} show={dockShown} />
    </>
  );
}
