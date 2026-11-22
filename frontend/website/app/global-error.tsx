"use client";

// Last resort: renders when the root layout itself fails, so it brings its own <html>, has no
// i18n or site styles, and keeps to plain inline CSS.
export default function GlobalError({ retry }: { error: unknown; retry: () => void }) {
  return (
    <html lang="en">
      <body
        style={{
          margin: 0,
          minHeight: "100vh",
          display: "flex",
          alignItems: "center",
          background: "#edf5ff",
          color: "#002d68",
          fontFamily: "Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif",
        }}
      >
        <main style={{ maxWidth: 640, margin: "0 auto", padding: "48px 24px" }}>
          <p style={{ margin: 0, fontSize: 13, letterSpacing: "0.14em", textTransform: "uppercase", color: "#0083ff" }}>
            500 · Server error
          </p>
          <h1 style={{ margin: "16px 0", fontSize: 44, lineHeight: 1.1, letterSpacing: "-0.02em" }}>
            We could not load this page
          </h1>
          <p style={{ margin: "0 0 28px", fontSize: 17, lineHeight: 1.6, color: "#44546a" }}>
            Something went wrong on our side. Try again in a moment.
          </p>
          <button
            type="button"
            onClick={() => retry()}
            style={{
              height: 48,
              padding: "0 28px",
              border: 0,
              borderRadius: 999,
              background: "#00bbff",
              color: "#001449",
              fontSize: 15,
              fontWeight: 500,
              cursor: "pointer",
            }}
          >
            Try again
          </button>
        </main>
      </body>
    </html>
  );
}
