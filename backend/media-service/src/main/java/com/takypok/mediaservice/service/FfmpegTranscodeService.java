package com.takypok.mediaservice.service;

import com.takypok.mediaservice.ffmpeg.FfmpegResolver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
@RequiredArgsConstructor
@Slf4j
public class FfmpegTranscodeService {

  // {label, width, height, videoBitrate, audioBitrate, bandwidth}
  private static final String[][] QUALITIES = {
    {"360p", "640", "360", "800k", "96k", "896000"},
    {"720p", "1280", "720", "2800k", "128k", "2928000"},
    {"1080p", "1920", "1080", "5000k", "192k", "5192000"},
  };

  private final FfmpegResolver ffmpegResolver;
  private final VideoStorageService storageService;
  private final Scheduler transcodeScheduler;

  /** Transcodes the video and returns only the quality rows that were actually produced. */
  public Mono<List<String[]>> transcode(String videoId) {
    return Mono.fromCallable(
            () -> {
              int sourceHeight = probeHeight(videoId);
              log.info("[FFmpeg] Source height for video {}: {}p", videoId, sourceHeight);

              List<String[]> applicable =
                  Arrays.stream(QUALITIES)
                      .filter(q -> Integer.parseInt(q[2]) <= sourceHeight)
                      .toList();

              if (applicable.isEmpty()) {
                // Fallback: always produce at least the lowest quality
                applicable = List.<String[]>of(QUALITIES[0]);
              }

              List<String> cmd = buildCommand(videoId, applicable);
              runProcess(videoId, cmd);
              return applicable;
            })
        // Bounded pool (see TranscodeSchedulerConfig) instead of Schedulers.boundedElastic() —
        // this is what actually enforces the concurrent-transcode cap, since this is where the
        // real FFmpeg process runs.
        .subscribeOn(transcodeScheduler);
  }

  private int probeHeight(String videoId) throws Exception {
    ProcessBuilder pb =
        new ProcessBuilder(
            ffmpegResolver.resolveProbe(),
            "-v",
            "error",
            "-select_streams",
            "v:0",
            "-show_entries",
            "stream=height",
            "-of",
            "default=noprint_wrappers=1:nokey=1",
            storageService.rawPath(videoId).toString());
    pb.redirectErrorStream(false);
    Process p = pb.start();

    String stdout = new String(p.getInputStream().readAllBytes()).trim();
    String stderr = new String(p.getErrorStream().readAllBytes()).trim();
    int exitCode = p.waitFor();

    if (!stderr.isEmpty()) {
      log.warn("[FFprobe] stderr for video {}: {}", videoId, stderr);
    }
    if (exitCode != 0 || stdout.isEmpty()) {
      throw new RuntimeException(
          "ffprobe failed for video " + videoId + " (exit=" + exitCode + "): " + stderr);
    }
    return Integer.parseInt(stdout);
  }

  private List<String> buildCommand(String videoId, List<String[]> qualities) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add(ffmpegResolver.resolve());
    cmd.add("-i");
    cmd.add(storageService.rawPath(videoId).toString());

    for (String[] q : qualities) {
      String label = q[0];
      String scale = q[1] + ":" + q[2];
      String vBitrate = q[3];
      String aBitrate = q[4];

      Files.createDirectories(storageService.qualityDir(videoId, label));
      String segmentPattern =
          storageService.qualityDir(videoId, label).resolve("seg_%03d.ts").toString();
      String playlist = storageService.qualityDir(videoId, label).resolve("index.m3u8").toString();

      cmd.addAll(
          List.of(
              "-map",
              "0:v",
              "-map",
              "0:a?",
              "-vf",
              "scale=" + scale,
              "-b:v",
              vBitrate,
              "-b:a",
              aBitrate,
              "-hls_time",
              "6",
              "-hls_playlist_type",
              "vod",
              "-hls_segment_filename",
              segmentPattern,
              playlist));
    }
    return cmd;
  }

  private void runProcess(String videoId, List<String> cmd) throws Exception {
    log.info("[FFmpeg] Starting transcode for video {}", videoId);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.redirectErrorStream(true);
    Process p = pb.start();

    // Drain stdout+stderr — required to prevent process hanging on full pipe buffer
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug("[FFmpeg] {}", line);
      }
    }

    int exitCode = p.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("FFmpeg exited with code " + exitCode + " for video " + videoId);
    }
    log.info("[FFmpeg] Transcode complete for video {}", videoId);
  }
}
