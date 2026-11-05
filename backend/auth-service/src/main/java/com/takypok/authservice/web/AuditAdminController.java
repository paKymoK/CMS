package com.takypok.authservice.web;

import com.takypok.authservice.model.entity.AuditAction;
import com.takypok.authservice.service.AuditService;
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
      @RequestParam(defaultValue = "0") int page,
      Model model) {
    int safePage = Math.max(page, 0);
    AuditService.Result result;
    try {
      result = auditService.search(q, action, from, to, safePage, PAGE_SIZE);
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
    model.addAttribute("actions", AuditAction.values());
    model.addAttribute("active", "audit");
    return "admin/audit";
  }
}
