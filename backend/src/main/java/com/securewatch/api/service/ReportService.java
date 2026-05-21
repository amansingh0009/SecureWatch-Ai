package com.securewatch.api.service;

import com.securewatch.api.entity.Report;
import com.securewatch.api.entity.Scan;
import com.securewatch.api.entity.User;
import com.securewatch.api.exception.ApiException;
import com.securewatch.api.repository.ReportRepository;
import com.securewatch.api.repository.ScanRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ScanRepository scanRepository;

    public ReportService(ReportRepository reportRepository, ScanRepository scanRepository) {
        this.reportRepository = reportRepository;
        this.scanRepository = scanRepository;
    }

    public Report create(User user, Long scanId) {
        Scan scan = scanRepository.findById(scanId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Scan not found"));
        Report report = new Report();
        report.setUser(user);
        report.setScan(scan);
        report.setTitle("Threat report for " + scan.getTarget());
        report.setSummary(scan.getAiSummary());
        return reportRepository.save(report);
    }

    public List<Report> list(User user) {
        return reportRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public byte[] exportMarkdown(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        String md = "# " + report.getTitle() + "\n\n" + report.getSummary() + "\n\nRisk score: " + report.getScan().getRiskScore();
        return md.getBytes(StandardCharsets.UTF_8);
    }
}
