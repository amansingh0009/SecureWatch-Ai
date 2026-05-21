package com.securewatch.api.controller;

import com.securewatch.api.dto.AdminDtos;
import com.securewatch.api.repository.ActivityLogRepository;
import com.securewatch.api.repository.ScanRepository;
import com.securewatch.api.repository.UserRepository;
import com.securewatch.api.util.Mapper;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ScanRepository scanRepository;

    public AdminController(UserRepository userRepository, ActivityLogRepository activityLogRepository, ScanRepository scanRepository) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.scanRepository = scanRepository;
    }

    @GetMapping("/users")
    public List<AdminDtos.UserSummary> users() {
        return userRepository.findAll().stream().map(Mapper::user).toList();
    }

    @GetMapping("/logs")
    public List<AdminDtos.ActivitySummary> logs() {
        return activityLogRepository.findTop50ByOrderByCreatedAtDesc().stream().map(Mapper::activity).toList();
    }

    @DeleteMapping("/scans/{id}")
    public void deleteScan(@PathVariable Long id) {
        scanRepository.deleteById(id);
    }
}
