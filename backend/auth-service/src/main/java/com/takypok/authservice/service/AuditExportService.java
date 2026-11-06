package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AuditAction;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;

/**
 * Writes the audit log as an .xlsx straight to an output stream. Rows are written in batches and
 * flushed after each one, so memory stays flat however many rows match (fastexcel streams a sheet
 * as long as nothing earlier is edited after a flush). Styles are applied per batch as one range,
 * not per cell. Times are UTC and the header says so.
 */
@Service
@RequiredArgsConstructor
public class AuditExportService {

  /** Excel's own limit is 1,048,576 rows; this keeps the file and the request a sensible size. */
  static final int MAX_ROWS = 100_000;

  private static final String HEADER_BG = "001449";
  private static final String HEADER_FG = "FFFFFF";
  private static final String DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
  private static final String[] HEADERS = {
    "When (UTC)", "Actor", "Action", "Account", "Detail", "IP"
  };
  private static final int[] WIDTHS = {21, 28, 26, 30, 40, 18};
  private static final int FLUSH_EVERY = 1000;

  private final AuditService auditService;

  /** Sets the download headers and writes the file to the response. */
  public void download(AuditService.Filter filter, HttpServletResponse response)
      throws IOException {
    auditService.validate(filter); // a bad filter fails here, before any header or byte is sent
    String stamp =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(ZonedDateTime.now(ZoneOffset.UTC));
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader(
        "Content-Disposition", "attachment; filename=\"account-audit-" + stamp + ".xlsx\"");
    response.setHeader("Cache-Control", "no-store");
    export(filter, response.getOutputStream());
  }

  /** Records that an export happened, then writes it. Returns the number of rows written. */
  int export(AuditService.Filter filter, OutputStream out) throws IOException {
    auditService.record(AuditAction.AUDIT_EXPORTED, null, describe(filter));

    Workbook wb = new Workbook(out, "auth-service", "1.0");
    Worksheet ws = wb.newWorksheet("Audit log");
    for (int c = 0; c < HEADERS.length; c++) {
      ws.value(0, c, HEADERS[c]);
      ws.width(c, WIDTHS[c]);
    }
    ws.range(0, 0, 0, HEADERS.length - 1)
        .style()
        .bold()
        .fillColor(HEADER_BG)
        .fontColor(HEADER_FG)
        .set();
    ws.freezePane(0, 1);

    int[] row = {1};
    int[] batchStart = {1};
    int written =
        auditService.stream(
            filter,
            MAX_ROWS,
            e -> {
              int r = row[0]++;
              ws.value(
                  r,
                  0,
                  e.at() == null
                      ? null
                      : e.at().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
              ws.value(r, 1, e.actor());
              ws.value(r, 2, e.actionLabel());
              ws.value(r, 3, e.target());
              ws.value(r, 4, e.detail());
              ws.value(r, 5, e.ip());
              if (row[0] - batchStart[0] >= FLUSH_EVERY) {
                closeBatch(ws, batchStart[0], row[0] - 1);
                batchStart[0] = row[0];
              }
            });
    if (row[0] > batchStart[0]) {
      closeBatch(ws, batchStart[0], row[0] - 1);
    }
    if (written >= MAX_ROWS) {
      ws.value(row[0], 0, "Stopped at " + MAX_ROWS + " rows. Narrow the filters to see the rest.");
    }
    wb.finish();
    return written;
  }

  /** Formats the date column of these rows in one call, then writes them out and frees them. */
  private void closeBatch(Worksheet ws, int firstRow, int lastRow) {
    try {
      ws.range(firstRow, 0, lastRow, 0).style().format(DATE_FORMAT).set();
      ws.flush();
    } catch (IOException ex) {
      throw new java.io.UncheckedIOException(ex);
    }
  }

  private static String describe(AuditService.Filter f) {
    StringBuilder sb = new StringBuilder("filters:");
    boolean any = false;
    if (f.q() != null && !f.q().isBlank()) {
      sb.append(" q=").append(f.q().trim());
      any = true;
    }
    if (f.action() != null && !f.action().isBlank()) {
      sb.append(" action=").append(f.action().trim());
      any = true;
    }
    if (f.from() != null) {
      sb.append(" from=").append(f.from());
      any = true;
    }
    if (f.to() != null) {
      sb.append(" to=").append(f.to());
      any = true;
    }
    return any ? sb.toString() : "filters: none";
  }
}
