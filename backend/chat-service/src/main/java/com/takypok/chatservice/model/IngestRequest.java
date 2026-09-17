package com.takypok.chatservice.model;

import lombok.Data;

/**
 * Optional single-file target. When fileName is blank/absent, either the given application's folder
 * (wipe-and-reload of just that application) or, if application is also blank, the whole documents
 * folder (wipe-and-reload of everything) is re-ingested.
 */
@Data
public class IngestRequest {
  private String fileName;
  private String application;
}
