package com.securewatch.api.repository;

import com.securewatch.api.entity.Scan;
import com.securewatch.api.entity.ScanType;
import com.securewatch.api.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScanRepository extends JpaRepository<Scan, Long> {
    List<Scan> findTop20ByUserOrderByCreatedAtDesc(User user);
    List<Scan> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
    long countByType(ScanType type);

    @Query("select coalesce(avg(s.riskScore), 0) from Scan s")
    double averageRiskScore();
}
