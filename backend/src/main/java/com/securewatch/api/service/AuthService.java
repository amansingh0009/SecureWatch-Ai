package com.securewatch.api.service;

import com.securewatch.api.dto.AuthDtos;
import com.securewatch.api.entity.Role;
import com.securewatch.api.entity.User;
import com.securewatch.api.exception.ApiException;
import com.securewatch.api.repository.UserRepository;
import com.securewatch.api.security.JwtService;
import com.securewatch.api.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ActivityService activityService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       ActivityService activityService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.activityService = activityService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request, HttpServletRequest servletRequest) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getRoles().add(userRepository.count() == 0 ? Role.ROLE_ADMIN : Role.ROLE_USER);
        User saved = userRepository.save(user);
        activityService.log(saved, "REGISTER", "New account created", servletRequest, false);
        return response(saved);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, HttpServletRequest servletRequest) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password()));
        } catch (BadCredentialsException ex) {
            activityService.log(user, "FAILED_LOGIN", "Failed login attempt", servletRequest, true);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        activityService.log(user, "LOGIN", "User logged in", servletRequest, false);
        return response(user);
    }

    private AuthDtos.AuthResponse response(User user) {
        String token = jwtService.generateToken(new UserPrincipal(user));
        Set<String> roles = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        return new AuthDtos.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), roles);
    }
}
