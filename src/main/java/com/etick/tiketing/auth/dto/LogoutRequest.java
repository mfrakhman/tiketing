package com.etick.tiketing.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank String refreshToken
) {
}
