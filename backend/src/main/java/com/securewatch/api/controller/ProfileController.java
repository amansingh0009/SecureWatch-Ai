package com.securewatch.api.controller;

import com.securewatch.api.service.CurrentUserService;
import com.securewatch.api.util.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final CurrentUserService currentUserService;

    public ProfileController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public Object profile() {
        return Mapper.user(currentUserService.get());
    }
}
