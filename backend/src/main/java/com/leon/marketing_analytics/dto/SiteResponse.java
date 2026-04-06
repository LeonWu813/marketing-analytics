package com.leon.marketing_analytics.dto;


public record SiteResponse(
        Long siteId,
        String siteCode,
        String siteName,
        String siteDomain
) {
}
