package com.etick.tiketing.auth.service;

import com.etick.tiketing.auth.dto.AuthResponse;
import com.etick.tiketing.auth.dto.LoginRequest;
import com.etick.tiketing.auth.dto.LogoutRequest;
import com.etick.tiketing.auth.dto.RegisterRequest;
import com.etick.tiketing.auth.security.JwtService;
import com.etick.tiketing.auth.token.entity.RefreshToken;
import com.etick.tiketing.auth.token.service.RefreshTokenService;
import com.etick.tiketing.auth.user.entity.User;
import com.etick.tiketing.auth.user.entity.UserStatus;
import com.etick.tiketing.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        return issueTokens(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken token = refreshTokenService.requireActive(request.refreshToken());
        refreshTokenService.revoke(token);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.issue(user);
        return new AuthResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessTokenTtlSeconds());
    }
}
