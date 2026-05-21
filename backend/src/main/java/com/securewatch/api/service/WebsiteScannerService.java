package com.securewatch.api.service;

import com.securewatch.api.entity.Scan;
import com.securewatch.api.entity.ScanStatus;
import com.securewatch.api.entity.ScanType;
import com.securewatch.api.entity.Severity;
import com.securewatch.api.entity.User;
import com.securewatch.api.entity.Vulnerability;
import com.securewatch.api.repository.ScanRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebsiteScannerService {
    private final ScanRepository scanRepository;
    private final AiThreatAnalysisService aiThreatAnalysisService;

    @Value("${app.scanner.allow-active-checks}")
    private boolean allowActiveChecks;

    public WebsiteScannerService(ScanRepository scanRepository, AiThreatAnalysisService aiThreatAnalysisService) {
        this.scanRepository = scanRepository;
        this.aiThreatAnalysisService = aiThreatAnalysisService;
    }

    public Scan scan(User user, String target) {
        Scan scan = new Scan();
        scan.setUser(user);
        scan.setType(ScanType.WEBSITE);
        scan.setStatus(ScanStatus.RUNNING);
        scan.setTarget(normalize(target));
        scan = scanRepository.save(scan);

        List<Vulnerability> findings = new ArrayList<>();
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                    .send(HttpRequest.newBuilder(URI.create(scan.getTarget()))
                            .timeout(Duration.ofSeconds(12))
                            .GET()
                            .build(), HttpResponse.BodyHandlers.ofString());

            checkHeaders(response, findings);
            checkServerDisclosure(response, findings);
            checkHttpMethods(scan.getTarget(), findings);
            if (allowActiveChecks) {
                passivePayloadHints(response.body(), findings);
            }
            if (!scan.getTarget().startsWith("https://")) {
                findings.add(v("Missing HTTPS", Severity.HIGH,
                        "The target is reachable over plain HTTP.",
                        "Redirect all traffic to HTTPS and enable HSTS.", "scheme=http"));
            }
        } catch (Exception ex) {
            findings.add(v("Target unreachable", Severity.MEDIUM,
                    "SecureWatch could not complete an HTTP request to the target.",
                    "Confirm DNS, firewall rules, and application availability.", ex.getMessage()));
        }

        for (Vulnerability finding : findings) {
            finding.setScan(scan);
            scan.getVulnerabilities().add(finding);
        }
        scan.setRiskScore(score(findings));
        scan.setAiSummary(aiThreatAnalysisService.analyze(scan.getTarget(), findings, scan.getRiskScore()));
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setCompletedAt(Instant.now());
        return scanRepository.save(scan);
    }

    private String normalize(String target) {
        if (!target.startsWith("http://") && !target.startsWith("https://")) return "https://" + target;
        return target;
    }

    private void checkHeaders(HttpResponse<String> response, List<Vulnerability> findings) {
        List<String> required = List.of("content-security-policy", "strict-transport-security", "x-content-type-options", "x-frame-options");
        for (String header : required) {
            if (response.headers().firstValue(header).isEmpty()) {
                findings.add(v("Missing security header: " + header, header.equals("content-security-policy") ? Severity.HIGH : Severity.MEDIUM,
                        "The response does not include " + header + ", reducing browser-side protection.",
                        "Add a tested " + header + " header at the edge or application layer.", "status=" + response.statusCode()));
            }
        }
    }

    private void checkServerDisclosure(HttpResponse<String> response, List<Vulnerability> findings) {
        response.headers().firstValue("server").ifPresent(server -> findings.add(v("Server banner disclosure", Severity.LOW,
                "The server header reveals implementation details: " + server,
                "Remove or minimize server/version banners.", server)));
    }

    private void checkHttpMethods(String target, List<Vulnerability> findings) {
        try {
            HttpResponse<String> options = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(target)).method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.ofString());
            String allow = options.headers().firstValue("allow").orElse("");
            if (allow.contains("PUT") || allow.contains("DELETE") || allow.contains("TRACE")) {
                findings.add(v("Insecure HTTP methods enabled", Severity.HIGH,
                        "Potentially dangerous HTTP methods are advertised by the target.",
                        "Disable PUT, DELETE, TRACE, and other unused methods on public endpoints.", allow));
            }
        } catch (Exception ignored) {
        }
    }

    private void passivePayloadHints(String body, List<Vulnerability> findings) {
        if (body != null && body.toLowerCase().contains("sql syntax")) {
            findings.add(v("SQL error disclosure", Severity.HIGH,
                    "The response appears to expose SQL error text.",
                    "Use parameterized queries and return generic error messages.", "sql syntax marker"));
        }
        if (body != null && body.contains("<script>alert(")) {
            findings.add(v("Reflected script marker", Severity.HIGH,
                    "A script marker appears in the response body.",
                    "Escape output and validate user-controlled input.", "script marker"));
        }
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

    private int score(List<Vulnerability> findings) {
        int score = findings.stream().mapToInt(v -> switch (v.getSeverity()) {
            case CRITICAL -> 35;
            case HIGH -> 25;
            case MEDIUM -> 12;
            case LOW -> 5;
        }).sum();
        return Math.min(100, score);
    }
}
