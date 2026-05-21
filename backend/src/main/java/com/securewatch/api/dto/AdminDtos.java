package com.securewatch.api.dto;

import java.time.Instant;
import java.util.Set;

public class AdminDtos {
    public record UserSummary(Long id, String name, String email, Set<String> roles, boolean enabled, Instant createdAt) {}
    public record ActivitySummary(Long id, String userEmail, String action, String details, String ipAddress, boolean suspicious, Instant createdAt) {}
}
