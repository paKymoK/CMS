package com.takypok.authservice.web;

import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.service.AuditExportService;
import com.takypok.authservice.service.AuditService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** The audit log screen (BRD USR-06), view-only. Same access rule as the Users screens. */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/audit")
public class AuditAdminController {

  private static final int PAGE_SIZE = 50;

  private final AuditService auditService;
  private final AuditExportService exportService;

  @Value("${auth.admin-app-url}")
  private String adminAppUrl;

  @ModelAttribute("cmsUrl")
  String cmsUrl() {
    return adminAppUrl;
  }

  @ModelAttribute("me")
  String me(Authentication authentication) {
    return authentication.getName();
  }

  @GetMapping
  public String list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String account,
      @RequestParam(defaultValue = "0") int page,
      Model model) {
    int safePage = Math.max(page, 0);
    AuditService.Result result;
    try {
      result =
          auditService.search(
              new AuditService.Filter(q, action, from, to, account), safePage, PAGE_SIZE);
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", "Unknown action filter.");
      result = new AuditService.Result(List.of(), 0);
    }
    model.addAttribute("rows", result.rows());
    model.addAttribute("total", result.total());
    model.addAttribute("page", safePage);
    model.addAttribute("totalPages", (result.total() + PAGE_SIZE - 1) / PAGE_SIZE);
    model.addAttribute("q", q == null ? "" : q);
    model.addAttribute("action", action == null ? "" : action);
    model.addAttribute("from", from == null ? "" : from.toString());
    model.addAttribute("to", to == null ? "" : to.toString());
    model.addAttribute("account", account == null ? "" : account.trim());
    model.addAttribute("actions", AuditAction.values());
    model.addAttribute("active", "audit");
    return "admin/audit";
  }

  /** The same filters as the list, as an .xlsx download. */
  @GetMapping("/export.xlsx")
  public void export(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String account,
      HttpServletResponse response)
      throws IOException {
    exportService.download(new AuditService.Filter(q, action, from, to, account), response);
  }
}
