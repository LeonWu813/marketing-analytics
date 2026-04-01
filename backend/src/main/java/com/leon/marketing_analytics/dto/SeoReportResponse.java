package com.leon.marketing_analytics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SeoReportResponse(
        Long id,
        String siteCode,
        String analyzedUrl,
        String keyword,
        LocalDateTime analyzedAt,
        List<SeoCheckResult> checks,

        // From pagespeed insight
        int performanceScore,
        int seoScore,
        String lcpSeconds,          // Largest Contentful Paint in seconds
        String fcpSeconds,          // First Contentful Paint in seconds
        String tbtMilliseconds,     // Total Blocking Time in milliseconds
        List<String> runWarnings
) {
}
