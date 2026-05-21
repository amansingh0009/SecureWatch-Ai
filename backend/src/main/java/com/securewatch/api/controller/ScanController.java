package com.securewatch.api.controller;

import com.securewatch.api.dto.ScanDtos;
import com.securewatch.api.repository.ScanRepository;
import com.securewatch.api.service.ActivityService;
import com.securewatch.api.service.CurrentUserService;
import com.securewatch.api.service.MalwareScannerService;
import com.securewatch.api.service.PortScannerService;
import com.securewatch.api.service.WebsiteScannerService;
import com.securewatch.api.util.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scans")
public class ScanController {
    private final CurrentUserService currentUserService;
    private final WebsiteScannerService websiteScannerService;
    private final PortScannerService portScannerService;
    private final MalwareScannerService malwareScannerService;
    private final ScanRepository scanRepository;
    private final ActivityService activityService;

    public ScanController(CurrentUserService currentUserService, WebsiteScannerService websiteScannerService,
                          PortScannerService portScannerService, MalwareScannerService malwareScannerService,
                          ScanRepository scanRepository, ActivityService activityService) {
        this.currentUserService = currentUserService;
        this.websiteScannerService = websiteScannerService;
        this.portScannerService = portScannerService;
        this.malwareScannerService = malwareScannerService;
        this.scanRepository = scanRepository;
        this.activityService = activityService;
    }

    @PostMapping("/website")
    public ScanDtos.ScanResponse website(@Valid @RequestBody ScanDtos.ScanRequest request, HttpServletRequest servletRequest) {
        var user = currentUserService.get();
        var scan = websiteScannerService.scan(user, request.target());
        activityService.log(user, "WEBSITE_SCAN", "Scanned " + request.target(), servletRequest, scan.getRiskScore() >= 70);
        return Mapper.scan(scan);
    }

    @PostMapping("/ports")
    public ScanDtos.ScanResponse ports(@Valid @RequestBody ScanDtos.ScanRequest request, HttpServletRequest servletRequest) {
        var user = currentUserService.get();
        var scan = portScannerService.scan(user, request.target());
        activityService.log(user, "PORT_SCAN", "Port scanned " + request.target(), servletRequest, scan.getRiskScore() >= 70);
        return Mapper.scan(scan);
    }

    @PostMapping("/malware")
    public ScanDtos.MalwareScanResponse malware(@RequestParam("file") MultipartFile file, HttpServletRequest servletRequest) {
        var user = currentUserService.get();
        var result = malwareScannerService.scan(file);
        activityService.log(user, "MALWARE_SCAN", "Scanned file hash " + result.sha256(), servletRequest, !"CLEAN".equals(result.verdict()));
        return result;
    }

    @GetMapping
    public List<ScanDtos.ScanResponse> history() {
        return scanRepository.findByUserOrderByCreatedAtDesc(currentUserService.get()).stream().map(Mapper::scan).toList();
    }
}
