package com.leon.marketing_analytics.dto;

import java.util.List;
import java.util.Map;

public record PageSpeedResult(
        Map<String, Object> rawResponse,
        int performanceScore,
        int seoScore,
        String lcpSeconds,          // Largest Contentful Paint in seconds
        String fcpSeconds,          // First Contentful Paint in seconds
        String tbtMilliseconds,     // Total Blocking Time in milliseconds
        List<String> warnings
) {
}
