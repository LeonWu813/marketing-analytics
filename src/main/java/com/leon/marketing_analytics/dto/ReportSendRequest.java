package com.leon.marketing_analytics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReportSendRequest(
        @NotBlank @Email String sentTo
) {
}
