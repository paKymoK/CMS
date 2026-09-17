package com.takypok.mediaservice.service;

import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class HlsPackagerService {

  private final VideoStorageService storageService;

  /**
   * qualities: rows from FfmpegTranscodeService.QUALITIES — {label, width, height, vBitrate,
   * aBitrate, bandwidth}
   */
  public Mono<Void> writeMasterPlaylist(String videoId, List<String[]> qualities) {
    return Mono.fromCallable(
            () -> {
              StringBuilder sb = new StringBuilder("#EXTM3U\n#EXT-X-VERSION:3\n");
              for (String[] q : qualities) {
                String label = q[0];
                String width = q[1];
                String height = q[2];
                String bandwidth = q[5];
                sb.append("\n#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(bandwidth)
                    .append(",RESOLUTION=")
                    .append(width)
                    .append("x")
                    .append(height)
                    .append("\n")
                    .append(label)
                    .append("/index.m3u8\n");
              }
              Files.createDirectories(storageService.hlsDir(videoId));
              Files.writeString(storageService.hlsDir(videoId).resolve("master.m3u8"), sb);
              log.info(
                  "[HLS] Master playlist written for video {} with qualities: {}",
                  videoId,
                  qualities.stream().map(q -> q[0]).toList());
              return (Void) null;
            })
        .subscribeOn(Schedulers.boundedElastic());
  }
}
