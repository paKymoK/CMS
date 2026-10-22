import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import Frame from "./Frame";
import "./frame.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Frame />
  </StrictMode>,
);
