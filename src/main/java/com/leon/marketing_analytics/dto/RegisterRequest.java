package com.leon.marketing_analytics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank
        @Size(min = 8, max = 30)
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$",
                message = "Password must contain at least one letter and one digit")
        String password
) {
}
