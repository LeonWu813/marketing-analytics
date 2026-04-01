package com.leon.marketing_analytics.dto;

import com.leon.marketing_analytics.entity.CheckStatus;

public record SeoCheckResult(
        String checkName,
        CheckStatus checkStatus,
        String details
) {
}
