import { useEffect, useState } from "react";
import { PostArticle } from "@cms/post-renderer";
import { MSG_POST, MSG_READY, PREVIEW_LABELS, type PreviewMessage } from "../lib/previewProtocol";

/**
 * The document the admin's post preview shows in an iframe. It holds no data of its own: the admin
 * sends the current editor state with postMessage and this draws it with the same PostArticle the
 * website uses. Being its own document, the breakpoints follow the frame's width (desktop / tablet /
 * mobile) and the website's styles never touch the admin UI.
 */
export default function Frame() {
  const [msg, setMsg] = useState<PreviewMessage | null>(null);

  useEffect(() => {
    const onMessage = (e: MessageEvent) => {
      if (e.origin !== window.location.origin) return;
      if ((e.data as PreviewMessage | undefined)?.type === MSG_POST) setMsg(e.data as PreviewMessage);
    };
    // Links in the preview must not navigate this frame away (a related-article link would load the admin SPA in it).
    const onClick = (e: MouseEvent) => {
      const a = (e.target as Element).closest("a");
      if (a && !a.getAttribute("href")?.startsWith("#")) e.preventDefault();
    };
    window.addEventListener("message", onMessage);
    document.addEventListener("click", onClick);
    window.parent.postMessage({ type: MSG_READY }, window.location.origin);
    return () => {
      window.removeEventListener("message", onMessage);
      document.removeEventListener("click", onClick);
    };
  }, []);

  if (!msg) return null;
  return <PostArticle post={msg.post} pageUrl={msg.pageUrl} labels={PREVIEW_LABELS} />;
}
