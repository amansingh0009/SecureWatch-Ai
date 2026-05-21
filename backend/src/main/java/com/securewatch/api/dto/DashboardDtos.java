package com.securewatch.api.dto;

import java.util.Map;

public class DashboardDtos {
    public record DashboardResponse(
            long totalScans,
            long criticalThreats,
            long highThreats,
            long suspiciousEvents,
            double averageRiskScore,
            Map<String, Long> scansByType,
            Map<String, Long> vulnerabilitiesBySeverity
    ) {}
}
