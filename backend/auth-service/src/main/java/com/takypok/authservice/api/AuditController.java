package com.takypok.authservice.api;

import com.takypok.authservice.service.AuditService;
import com.takypok.core.model.PageResponse;
import com.takypok.core.model.ResultMessage;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only view of the account audit trail. Super Admin only (ApiSecurityConfig). */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/audit")
public class AuditController {
  private final AuditService auditService;

  @GetMapping
  public ResponseEntity<ResultMessage<PageResponse<AuditService.Entry>>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    AuditService.Result result = auditService.search(q, action, from, to, page, size);
    int safePage = Math.max(page, 0);
    int safeSize = Math.min(Math.max(size, 1), 200);
    return ResponseEntity.ok(
        ResultMessage.success(
            PageResponse.<AuditService.Entry>builder()
                .content(result.rows())
                .page(safePage)
                .size(safeSize)
                .totalElements(result.total())
                .totalPages((result.total() + safeSize - 1) / safeSize)
                .build()));
  }
}
