package com.takypok.mediaservice.service;

import com.takypok.mediaservice.model.dto.Base64ChunkRequest;
import com.takypok.mediaservice.model.dto.ChunkAckResponse;
import com.takypok.mediaservice.model.dto.ChunkedUploadedFile;
import com.takypok.mediaservice.model.dto.FinishChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadResponse;
import com.takypok.mediaservice.model.entity.UploadFile;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChunkedUploadService {
  Mono<StartChunkedUploadResponse> start(StartChunkedUploadRequest request);

  Mono<ChunkAckResponse> writeChunk(String sessionId, int index, Flux<DataBuffer> body);

  /**
   * Same as {@link #writeChunk}, but takes the chunk as a base64-encoded string in a JSON body
   * instead of a raw binary stream. Some corporate proxies/gateways content-inspect raw
   * octet-stream bodies and block ones that look like a recognizable binary format (e.g. the ZIP
   * signature at the start of an .xlsx/.docx) — base64 text sidesteps that.
   */
  Mono<ChunkAckResponse> writeChunkBase64(String sessionId, int index, Base64ChunkRequest request);

  /**
   * Same as {@link #writeChunk}, but the body is AES-GCM ciphertext (12-byte IV + ciphertext +
   * 16-byte tag) encrypted client-side with a key shared out of band with the frontend. Unlike
   * {@link #writeChunkBase64}, this isn't just re-encoding — a proxy that decodes/inspects the
   * payload still sees indistinguishable-from-random bytes, not the original file signature, so it
   * survives content-inspecting gateways that base64 alone does not.
   */
  Mono<ChunkAckResponse> writeChunkEncrypted(String sessionId, int index, Flux<DataBuffer> body);

  Mono<UploadFile> finish(String sessionId, FinishChunkedUploadRequest request);

  Mono<List<ChunkedUploadedFile>> listFiles();

  /**
   * Deletes one finished upload by its on-disk name (as returned by {@link #listFiles()}'s {@code
   * name}), removing both the file and its {@link UploadFile} row. A name that isn't currently on
   * disk is treated as already-deleted rather than an error, so a stale/double click is harmless.
   */
  Mono<Void> deleteFile(String name);

  /** Deletes every finished upload currently in the files directory (file + DB row each). */
  Mono<Void> deleteAllFiles();
}
