package com.securewatch.api.service;

import com.securewatch.api.entity.Scan;
import com.securewatch.api.entity.ScanStatus;
import com.securewatch.api.entity.ScanType;
import com.securewatch.api.entity.Severity;
import com.securewatch.api.entity.User;
import com.securewatch.api.entity.Vulnerability;
import com.securewatch.api.repository.ScanRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PortScannerService {
    private final ScanRepository scanRepository;
    private final AiThreatAnalysisService aiThreatAnalysisService;

    @Value("${app.scanner.nmap-path}")
    private String nmapPath;

    public PortScannerService(ScanRepository scanRepository, AiThreatAnalysisService aiThreatAnalysisService) {
        this.scanRepository = scanRepository;
        this.aiThreatAnalysisService = aiThreatAnalysisService;
    }

    public Scan scan(User user, String target) {
        Scan scan = new Scan();
        scan.setUser(user);
        scan.setType(ScanType.PORT);
        scan.setStatus(ScanStatus.RUNNING);
        scan.setTarget(target);
        scan = scanRepository.save(scan);

        List<String> lines = runNmap(target);
        List<Vulnerability> findings = new ArrayList<>();
        int openPorts = 0;
        for (String line : lines) {
            if (line.contains("/tcp") && line.contains("open")) {
                openPorts++;
                Severity severity = riskyPort(line) ? Severity.HIGH : Severity.MEDIUM;
                Vulnerability finding = v("Open service: " + line.trim(), severity,
                        "An externally reachable service was detected.",
                        "Restrict exposure with firewalls, close unused services, and require strong authentication.", line.trim());
                finding.setScan(scan);
                findings.add(finding);
            }
        }
        if (lines.isEmpty()) {
            Vulnerability fallback = v("Nmap unavailable or no result", Severity.LOW,
                    "No Nmap output was captured. Install Nmap or verify target reachability.",
                    "Set NMAP_PATH and ensure the runtime can execute Nmap.", "empty output");
            fallback.setScan(scan);
            findings.add(fallback);
        }
        scan.getVulnerabilities().addAll(findings);
        scan.setOpenPorts(openPorts);
        scan.setRiskScore(Math.min(100, openPorts * 12 + (int) findings.stream().filter(f -> f.getSeverity() == Severity.HIGH).count() * 15));
        scan.setMetadataJson("{\"nmapLines\":" + lines.size() + "}");
        scan.setAiSummary(aiThreatAnalysisService.analyze(target, findings, scan.getRiskScore()));
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setCompletedAt(Instant.now());
        return scanRepository.save(scan);
    }

    private List<String> runNmap(String target) {
        List<String> lines = new ArrayList<>();
        try {
            Process process = new ProcessBuilder(nmapPath, "-sV", "-T3", "--top-ports", "100", target).redirectErrorStream(true).start();
            boolean done = process.waitFor(40, TimeUnit.SECONDS);
            if (!done) process.destroyForcibly();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) lines.add(line);
            }
        } catch (Exception ignored) {
        }
        return lines;
    }

    private boolean riskyPort(String line) {
        return line.startsWith("21/") || line.startsWith("23/") || line.startsWith("3389/") || line.startsWith("3306/");
    }

    private Vulnerability v(String name, Severity severity, String description, String recommendation, String evidence) {
        Vulnerability vulnerability = new Vulnerability();
        vulnerability.setName(name);
        vulnerability.setSeverity(severity);
        vulnerability.setDescription(description);
        vulnerability.setRecommendation(recommendation);
        vulnerability.setEvidence(evidence);
        return vulnerability;
    }
}
