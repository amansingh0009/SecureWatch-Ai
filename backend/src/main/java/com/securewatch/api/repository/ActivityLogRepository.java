package com.securewatch.api.repository;

import com.securewatch.api.entity.ActivityLog;
import com.securewatch.api.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop50ByOrderByCreatedAtDesc();
    List<ActivityLog> findTop50ByUserOrderByCreatedAtDesc(User user);
    long countBySuspiciousTrue();
}
