package com.leon.marketing_analytics.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record EventIngestRequest(
        @NotBlank String eventType,
        @NotBlank String pageUrl,
        @NotBlank String siteCode,
        String utmSource,
        String utmMedium,
        String utmCampaign,
        Long campaignId,
        String userIdentifier,
        Map<String, Object> metadata
) {
}
