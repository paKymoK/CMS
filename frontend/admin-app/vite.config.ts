import { resolve } from "node:path";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    dedupe: ["react", "react-dom", "react-router-dom"],
  },
  build: {
    rollupOptions: {
      // Two pages: the admin app, and the iframe document the post preview renders in.
      input: {
        main: resolve(__dirname, "index.html"),
        previewFrame: resolve(__dirname, "preview-frame.html"),
      },
    },
  },
  server: {
    port: 5173,
  },
});
