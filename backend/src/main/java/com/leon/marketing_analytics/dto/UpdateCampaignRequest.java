package com.leon.marketing_analytics.dto;

import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCampaignRequest(
        @NotBlank String campaignName,
        String campaignDescription,
        @Positive BigDecimal cost,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull CampaignStatus status,
        @NotNull CampaignChannel channel,
        String metricName,
        BigDecimal metricValue,
        String benchmarkMetricName,
        BigDecimal benchmarkMetricValue
) {
}