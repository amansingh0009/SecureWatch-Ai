package com.securewatch.api.repository;

import com.securewatch.api.entity.Report;
import com.securewatch.api.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserOrderByCreatedAtDesc(User user);
}
