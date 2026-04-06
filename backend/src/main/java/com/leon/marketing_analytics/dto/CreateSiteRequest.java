package com.leon.marketing_analytics.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSiteRequest(
        @NotBlank String siteName,
        @NotBlank String siteDomain
) {
}
