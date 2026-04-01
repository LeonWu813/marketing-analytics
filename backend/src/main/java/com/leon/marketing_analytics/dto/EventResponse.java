package com.leon.marketing_analytics.dto;

import com.leon.marketing_analytics.entity.CampaignChannel;

import java.time.LocalDateTime;
import java.util.Map;

public record EventResponse(
        Long id,
        String eventType,
        String pageUrl,
        Long campaignId,
        String utmSource,
        String utmMedium,
        String utmCampaign,
        CampaignChannel channel,
        String country,
        String userIdentifier,
        Map<String, Object> metadata,
        LocalDateTime createdAt
) {
}
