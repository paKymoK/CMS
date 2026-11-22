import { stir } from "./simulate";

// Simulates a stirring off the main thread and hands the recording back
self.onmessage = ({ data: { cols, rows } }: MessageEvent<{ cols: number; rows: number }>) => {
  const record = stir(cols, rows);
  (self as unknown as Worker).postMessage({ record, cols, rows }, [record.buffer]);
};
