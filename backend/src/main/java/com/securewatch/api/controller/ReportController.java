package com.securewatch.api.controller;

import com.securewatch.api.entity.Report;
import com.securewatch.api.service.CurrentUserService;
import com.securewatch.api.service.ReportService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final CurrentUserService currentUserService;

    public ReportController(ReportService reportService, CurrentUserService currentUserService) {
        this.reportService = reportService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/scan/{scanId}")
    public Map<String, Object> create(@PathVariable Long scanId) {
        Report report = reportService.create(currentUserService.get(), scanId);
        return Map.of("id", report.getId(), "title", report.getTitle(), "summary", report.getSummary(), "createdAt", report.getCreatedAt());
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return reportService.list(currentUserService.get()).stream()
                .map(r -> Map.<String, Object>of("id", r.getId(), "title", r.getTitle(), "summary", r.getSummary(), "createdAt", r.getCreatedAt()))
                .toList();
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=securewatch-report-" + id + ".md")
                .contentType(MediaType.TEXT_MARKDOWN)
                .body(reportService.exportMarkdown(id));
    }
}
