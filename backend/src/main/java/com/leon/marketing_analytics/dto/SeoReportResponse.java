package com.leon.marketing_analytics.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record SeoReportResponse(
        Long id,
        String siteCode,
        String analyzedUrl,
        String keyword,
        String title,
        String metaDescription,
        LocalDateTime analyzedAt,
        List<SeoCheckResult> checks,

        // From pagespeed insight
        int performanceScore,
        int seoScore,
        String lcpSeconds,          // Largest Contentful Paint in seconds
        String fcpSeconds,          // First Contentful Paint in seconds
        String tbtMilliseconds,     // Total Blocking Time in milliseconds
        String loadingExperience,
        List<Map<String, Object>> seoAudits,
        List<Map<String, Object>> opportunities,
        List<String> runWarnings
) {
}
