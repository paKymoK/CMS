"use client";

import { useEffect, useRef, useState } from "react";
import { useLocale } from "next-intl";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

const GREETING =
  "Thank you for checking out our services. If you don’t mind me asking, what brings you to CMC Global today?";

const CHIPS = [
  { label: "Tell me about CMC", send: "Tell me about CMC Global" },
  { label: "What services do you have?", send: "What services do you offer?" },
  { label: "Know more AI capabilities", send: "What are your AI capabilities?" },
];

const FALLBACK_REPLY =
  "Sorry, I'm having trouble reaching our knowledge base right now — please try again in a moment, or reach out to our team directly.";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

type Message = { role: "user" | "bot"; text: string };
type AssistantTurn = { role: "USER" | "ASSISTANT"; content: string };

/**
 * chat-service's PublicAssistantController is stateless — it doesn't persist a session, so the
 * widget resends its own running transcript on every call. Same gateway-routed base as
 * lib/cms/homeContent.ts (never straight to chat-service's own port); see AuthenticationConfig's
 * POST /chat-service/v1/assistant/ask permitAll carve-out for why this is safe unauthenticated.
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

export function AssistantPanel() {
  const reducedMotion = usePrefersReducedMotion();
  const locale = useLocale();
  const logRef = useRef<HTMLDivElement>(null);
  // Sent back on every call so chat-service (stateless — see PublicAskRequest) has the running
  // transcript. Only updated on a successful reply — a client-side fallback message never enters
  // the history sent to the backend.
  const historyRef = useRef<AssistantTurn[]>([]);

  const [greetingText, setGreetingText] = useState("");
  const [greetingDone, setGreetingDone] = useState(false);
  const [chipsVisible, setChipsVisible] = useState<boolean[]>([false, false, false]);
  const [messages, setMessages] = useState<Message[]>([]);
  const [busy, setBusy] = useState(false);
  const [slow, setSlow] = useState(false);
  const [streamingText, setStreamingText] = useState<string | null>(null);
  const [input, setInput] = useState("");

  // Greeting: typed at 22ms/char after a 420ms delay, with a blinking caret
  // that hides on completion. Reduced motion skips this effect entirely —
  // the JSX below reads `reducedMotion` directly instead of mirroring it
  // into state, so there's nothing to synchronously set here.
  useEffect(() => {
    if (reducedMotion) return;
    let cancelled = false;
    const timers: ReturnType<typeof setTimeout>[] = [];
    timers.push(
      setTimeout(() => {
        let i = 0;
        const tick = () => {
          if (cancelled) return;
          i += 1;
          setGreetingText(GREETING.slice(0, i));
          if (i < GREETING.length) {
            timers.push(setTimeout(tick, 22));
          } else {
            setGreetingDone(true);
            CHIPS.forEach((_, idx) => {
              timers.push(
                setTimeout(() => {
                  setChipsVisible((prev) => {
                    const next = [...prev];
                    next[idx] = true;
                    return next;
                  });
                }, idx * 60),
              );
            });
          }
        };
        tick();
      }, 420),
    );
    return () => {
      cancelled = true;
      timers.forEach(clearTimeout);
    };
  }, [reducedMotion]);

  useEffect(() => {
    logRef.current?.scrollTo({ top: logRef.current.scrollHeight });
  }, [messages, streamingText]);

  async function sendMessage(text: string) {
    const trimmed = text.trim();
    if (!trimmed || busy) return;
    setMessages((prev) => [...prev, { role: "user", text: trimmed }]);
    setInput("");
    setBusy(true);
    setStreamingText(null);

    const slowTimer = setTimeout(() => setSlow(true), 1500);

    let reply: string;
    let ok = true;
    try {
      const result = await askAssistant(locale, trimmed, historyRef.current);
      reply = result.answer;
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

  return (
    <div className="relative px-6 pt-4 pb-6 text-center" style={{ zIndex: 2 }}>
      <div
        aria-hidden
        className="pointer-events-none absolute inset-x-0 top-0 h-px"
        style={{
          background: "linear-gradient(90deg, transparent, rgba(170,212,255,.34), transparent)",
        }}
      />
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0"
        style={{
          background:
            "linear-gradient(180deg, rgba(150,195,255,.09), rgba(120,175,255,.03) 30%, rgba(8,32,74,.1))",
        }}
      />

      <div className="relative mx-auto max-w-[860px]">
        <div
          aria-hidden
          className="mx-auto mb-4 h-10 w-10 rounded-full"
          style={{
            background:
              "radial-gradient(circle at 34% 30%, #eaf6ff 0%, #7cc4f7 42%, #1a6fc4 78%, #0c3f7d 100%)",
            boxShadow: "0 0 22px rgba(90,180,255,.55)",
          }}
        />
        <h2 className="mb-6 text-[clamp(18px,2.4vw,26px)] font-bold text-white">
          The Intelligent Way To Know More About CMC
        </h2>

        <div
          className="mx-auto ml-0 max-w-[620px] rounded-[10px] border border-white/26 p-[18px] text-left backdrop-blur-[16px] backdrop-saturate-[120%]"
          style={{
            background: "linear-gradient(105deg, rgba(214,228,245,.2), rgba(150,180,215,.1))",
            boxShadow: "0 18px 44px rgba(3,12,32,.34)",
          }}
        >
          <div
            ref={logRef}
            className="flex flex-col gap-3 overflow-y-auto overflow-x-hidden pr-2"
            style={{ height: 172 }}
          >
            <p className="text-[14px] leading-[1.55] text-[#dce7f5]">
              {reducedMotion ? GREETING : greetingText}
              {!greetingDone && !reducedMotion && (
                <span
                  aria-hidden
                  className="caret-blink ml-0.5 inline-block w-[7px] bg-brand-accent align-middle"
                  style={{ height: 15 }}
                />
              )}
            </p>

            {messages.map((m, i) => (
              <p
                key={i}
                className={
                  m.role === "user"
                    ? "ml-auto max-w-[78%] rounded-[3px] bg-[#0b63c5] px-3.5 py-2.5 text-[13.5px] leading-[1.5] text-white"
                    : "max-w-[88%] rounded-[3px] bg-white/8 px-3.5 py-2.5 text-[13.5px] leading-[1.55] whitespace-pre-wrap text-[#dce7f5]"
                }
              >
                {m.text}
              </p>
            ))}

            {busy && streamingText === null && (
              <div className="flex gap-1.5" aria-label="Assistant is typing">
                {[0, 1, 2].map((i) => (
                  <span
                    key={i}
                    className="dot-pulse h-1.5 w-1.5 rounded-full bg-white/60"
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
              <p className="max-w-[88%] rounded-[3px] bg-white/8 px-3.5 py-2.5 text-[13.5px] leading-[1.55] whitespace-pre-wrap text-[#dce7f5]">
                {streamingText}
              </p>
            )}
          </div>
        </div>

        <div className="mt-3 flex flex-wrap justify-end gap-2">
          {CHIPS.map((chip, i) => (
            <button
              key={chip.label}
              type="button"
              onClick={() => sendMessage(chip.send)}
              className="min-h-9 rounded-full bg-white px-4 py-2.5 text-[11.5px] text-[#123a63] transition-[opacity,transform,background] duration-200 hover:bg-[#e8f1fb]"
              style={{
                opacity: reducedMotion || chipsVisible[i] ? 1 : 0,
                transform: reducedMotion || chipsVisible[i] ? "none" : "translateY(10px)",
              }}
            >
              {chip.label}
            </button>
          ))}

          <form
            className="flex items-center gap-2"
            onSubmit={(e) => {
              e.preventDefault();
              sendMessage(input);
            }}
          >
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask a question"
              className="h-9 w-[150px] rounded-full border border-white/28 bg-white/10 px-3.5 text-[12.5px] text-white placeholder:text-white/50 outline-none focus:border-[#8fd0ff]"
            />
            <button
              type="submit"
              disabled={busy}
              className="flex h-9 w-9 flex-none items-center justify-center rounded-full bg-[#0b63c5] text-white disabled:opacity-50"
              aria-label="Send"
            >
              <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current stroke-2" aria-hidden>
                <path d="M5 12h14M13 6l6 6-6 6" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
