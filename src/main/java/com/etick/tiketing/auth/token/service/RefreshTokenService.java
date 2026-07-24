package com.etick.tiketing.auth.token.service;

import com.etick.tiketing.auth.token.entity.RefreshToken;
import com.etick.tiketing.auth.token.repository.RefreshTokenRepository;
import com.etick.tiketing.auth.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-ttl-days}")
    private long refreshTokenTtlDays;

    public String issue(User user) {
        String rawToken = generateRawToken();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(Instant.now().plus(refreshTokenTtlDays, ChronoUnit.DAYS));
        refreshTokenRepository.save(token);

        return rawToken;
    }

    public RefreshToken requireActive(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (token.getRevokedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is no longer valid");
        }

        return token;
    }

    public void revoke(RefreshToken token) {
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
