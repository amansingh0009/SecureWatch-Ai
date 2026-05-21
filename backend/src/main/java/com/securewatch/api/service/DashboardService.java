package com.securewatch.api.service;

import com.securewatch.api.dto.DashboardDtos;
import com.securewatch.api.entity.ScanType;
import com.securewatch.api.entity.Severity;
import com.securewatch.api.repository.ActivityLogRepository;
import com.securewatch.api.repository.ScanRepository;
import com.securewatch.api.repository.VulnerabilityRepository;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ScanRepository scanRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final ActivityLogRepository activityLogRepository;

    public DashboardService(ScanRepository scanRepository, VulnerabilityRepository vulnerabilityRepository,
                            ActivityLogRepository activityLogRepository) {
        this.scanRepository = scanRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public DashboardDtos.DashboardResponse stats() {
        Map<String, Long> byType = Arrays.stream(ScanType.values())
                .collect(Collectors.toMap(Enum::name, scanRepository::countByType));
        Map<String, Long> bySeverity = Arrays.stream(Severity.values())
                .collect(Collectors.toMap(Enum::name, vulnerabilityRepository::countBySeverity));
        return new DashboardDtos.DashboardResponse(
                scanRepository.count(),
                bySeverity.getOrDefault("CRITICAL", 0L),
                bySeverity.getOrDefault("HIGH", 0L),
                activityLogRepository.countBySuspiciousTrue(),
                scanRepository.averageRiskScore(),
                byType,
                bySeverity
        );
    }
}
