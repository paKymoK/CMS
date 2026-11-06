package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.takypok.authservice.model.entity.AuditAction;
import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class AuditExportServiceTest {

  AuditService audit = mock(AuditService.class);
  AuditExportService service = new AuditExportService(audit);
  AuditService.Filter none = new AuditService.Filter(null, null, null, null);

  /** Feeds {@code n} entries to the sink, as AuditService.stream would. */
  @SuppressWarnings("unchecked")
  private void feed(int n) {
    doAnswer(
        inv -> {
          Consumer<AuditService.Entry> sink = inv.getArgument(2);
          for (int i = 0; i < n; i++) {
            sink.accept(
                new AuditService.Entry(
                    i,
                    ZonedDateTime.of(2026, 11, 5, 16, 22, 15, 0, ZoneOffset.UTC),
                    "admin",
                    "LOGIN_SUCCESS",
                    "Signed in",
                    "=cmd|' /C calc'!A0", // must stay text, never a formula
                    "a & b <c> \u0001 end",
                    "10.0.0.1",
                    "Admin (admin)",
                    "=cmd|' /C calc'!A0"));
          }
          return n;
        })
        .when(audit)
        .stream(any(), anyInt(), any());
  }

  /** Reads the first sheet back with a real xlsx reader. */
  private static java.util.List<org.dhatim.fastexcel.reader.Row> rowsOf(byte[] xlsx)
      throws Exception {
    java.nio.file.Path file = java.nio.file.Files.createTempFile("audit-export", ".xlsx");
    try {
      java.nio.file.Files.write(file, xlsx);
      try (var wb = new org.dhatim.fastexcel.reader.ReadableWorkbook(file.toFile());
          var rows = wb.getFirstSheet().openStream()) {
        return rows.toList();
      }
    } finally {
      java.nio.file.Files.deleteIfExists(file);
    }
  }

  @Test
  void writesAHeaderAndEveryRowAcrossSeveralFlushBatches() throws Exception {
    feed(2500);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    int written = service.export(none, out);

    var rows = rowsOf(out.toByteArray());
    assertThat(written).isEqualTo(2500);
    assertThat(rows).hasSize(2501); // header + rows
    assertThat(rows.get(0).getCellText(0)).isEqualTo("When (UTC)");
    assertThat(rows.get(0).getCellText(5)).isEqualTo("IP");
    assertThat(rows.get(2500).getCellText(2)).isEqualTo("Signed in");
    assertThat(rows.get(1).getCellText(1))
        .isEqualTo("Admin (admin)"); // the label, not the bare sub
    verify(audit).record(AuditAction.AUDIT_EXPORTED, null, "filters: none");
  }

  @Test
  void textStaysTextAndTheTimeIsARealUtcDate() throws Exception {
    feed(1);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    service.export(none, out);

    var row = rowsOf(out.toByteArray()).get(1);
    assertThat(row.getCell(3).getType()).isEqualTo(org.dhatim.fastexcel.reader.CellType.STRING);
    assertThat(row.getCellText(3)).isEqualTo("=cmd|' /C calc'!A0");
    assertThat(row.getCellText(4)).startsWith("a & b <c>");
    assertThat(row.getCellText(4)).doesNotContain("\u0001");
    assertThat(row.getCell(0).getType()).isEqualTo(org.dhatim.fastexcel.reader.CellType.NUMBER);
    assertThat(row.getCellAsDate(0).orElseThrow())
        .isEqualTo(java.time.LocalDateTime.of(2026, 11, 5, 16, 22, 15));
  }

  @Test
  void stopsAtTheRowCapAndSaysSo() throws Exception {
    feed(AuditExportService.MAX_ROWS);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    service.export(none, out);

    var rows = rowsOf(out.toByteArray());
    assertThat(rows).hasSize(AuditExportService.MAX_ROWS + 2); // header + rows + the note
    assertThat(rows.get(rows.size() - 1).getCellText(0)).startsWith("Stopped at 100000 rows");
  }

  @Test
  void recordsWhichFiltersWereUsed() throws Exception {
    feed(0);

    service.export(
        new AuditService.Filter("lan", "LOGIN_FAILED", java.time.LocalDate.of(2026, 1, 2), null),
        new ByteArrayOutputStream());

    verify(audit)
        .record(
            AuditAction.AUDIT_EXPORTED, null, "filters: q=lan action=LOGIN_FAILED from=2026-01-02");
  }

  @Test
  void aBadFilterIsRefusedBeforeAnythingIsWritten() throws Exception {
    doThrow(new IllegalArgumentException("bad")).when(audit).validate(any());
    var response = new org.springframework.mock.web.MockHttpServletResponse();

    assertThatThrownBy(() -> service.download(none, response))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(response.getContentAsByteArray()).isEmpty();
    assertThat(response.getContentType()).isNull();
    verify(audit, never()).record(any(), any(), any());
  }

  @Test
  void downloadSetsXlsxHeaders() throws Exception {
    feed(1);
    var response = new org.springframework.mock.web.MockHttpServletResponse();

    service.download(none, response);

    assertThat(response.getContentType())
        .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    assertThat(response.getHeader("Content-Disposition"))
        .startsWith("attachment; filename=\"account-audit-")
        .endsWith(".xlsx\"");
    assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
  }
}
