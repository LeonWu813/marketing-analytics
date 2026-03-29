package com.leon.marketing_analytics.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record SeoAnalyzeRequest (
        @NotBlank @URL String analyzedUrl,
        String keyword,
        boolean isFollowUp
) {
}
