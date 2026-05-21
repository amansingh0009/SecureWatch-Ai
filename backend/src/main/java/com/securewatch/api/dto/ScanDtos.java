package com.securewatch.api.dto;

import com.securewatch.api.entity.ScanStatus;
import com.securewatch.api.entity.ScanType;
import com.securewatch.api.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

public class ScanDtos {
    public record ScanRequest(@NotBlank String target) {}
    public record MalwareScanResponse(String sha256, String verdict, int malicious, int suspicious, String permalink) {}

    public record VulnerabilityDto(
            Long id,
            String name,
            Severity severity,
            String description,
            String recommendation,
            String evidence
    ) {}

    public record ScanResponse(
            Long id,
            ScanType type,
            ScanStatus status,
            String target,
            int riskScore,
            int openPorts,
            String aiSummary,
            String metadataJson,
            Instant createdAt,
            Instant completedAt,
            List<VulnerabilityDto> vulnerabilities
    ) {}
}
