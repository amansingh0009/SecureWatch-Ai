package com.securewatch.api.util;

import com.securewatch.api.dto.AdminDtos;
import com.securewatch.api.dto.ScanDtos;
import com.securewatch.api.entity.ActivityLog;
import com.securewatch.api.entity.Role;
import com.securewatch.api.entity.Scan;
import com.securewatch.api.entity.User;

public final class Mapper {
    private Mapper() {}

    public static ScanDtos.ScanResponse scan(Scan scan) {
        return new ScanDtos.ScanResponse(
                scan.getId(),
                scan.getType(),
                scan.getStatus(),
                scan.getTarget(),
                scan.getRiskScore(),
                scan.getOpenPorts(),
                scan.getAiSummary(),
                scan.getMetadataJson(),
                scan.getCreatedAt(),
                scan.getCompletedAt(),
                scan.getVulnerabilities().stream().map(v -> new ScanDtos.VulnerabilityDto(
                        v.getId(), v.getName(), v.getSeverity(), v.getDescription(), v.getRecommendation(), v.getEvidence()
                )).toList()
        );
    }

    public static AdminDtos.UserSummary user(User user) {
        return new AdminDtos.UserSummary(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream().map(Role::name).collect(java.util.stream.Collectors.toSet()),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }

    public static AdminDtos.ActivitySummary activity(ActivityLog log) {
        return new AdminDtos.ActivitySummary(
                log.getId(),
                log.getUser() == null ? null : log.getUser().getEmail(),
                log.getAction(),
                log.getDetails(),
                log.getIpAddress(),
                log.isSuspicious(),
                log.getCreatedAt()
        );
    }
}
