package com.securewatch.api.service;

import com.securewatch.api.entity.ActivityLog;
import com.securewatch.api.entity.User;
import com.securewatch.api.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityLogRepository activityLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ActivityService(ActivityLogRepository activityLogRepository, SimpMessagingTemplate messagingTemplate) {
        this.activityLogRepository = activityLogRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ActivityLog log(User user, String action, String details, HttpServletRequest request, boolean suspicious) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(clientIp(request));
        log.setSuspicious(suspicious);
        ActivityLog saved = activityLogRepository.save(log);
        if (suspicious) {
            messagingTemplate.convertAndSend("/topic/alerts", details);
        }
        return saved;
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) return "system";
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
