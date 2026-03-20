package com.leon.marketing_analytics.dto;

import com.leon.marketing_analytics.entity.User;
import jakarta.validation.constraints.NotBlank;

public record SiteResquest (
        @NotBlank Long siteId,
        @NotBlank String siteCode,
        @NotBlank String name,
        @NotBlank User user
) {
}
