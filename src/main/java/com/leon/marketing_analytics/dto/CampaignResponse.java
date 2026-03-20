package com.leon.marketing_analytics.dto;

import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CampaignResponse(
        Long id,
        LocalDateTime createAt,
        String campaignName,
        String campaignDescription,
        BigDecimal cost,
        LocalDate startDate,
        LocalDate endDate,
        CampaignStatus status,
        CampaignChannel channel,
        String metricName,
        BigDecimal metricValue,
        String benchmarkMetricName,
        BigDecimal benchmarkMetricValue,
        boolean isArchived
) {
}