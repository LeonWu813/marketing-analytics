package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.DailyCount;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.repository.AnalyticsRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final SiteRepository siteRepository;

    private Site validateSiteOwnership(String siteCode, User user) {
        return siteRepository.findBySiteCodeAndUser(siteCode, user).orElseThrow(
                () -> new ForbiddenException("Forbidden access."));
    }

    @Transactional(readOnly = true)
    public Long getTotalEvents(String siteCode,
                               User user,
                               LocalDateTime fromDate,
                               LocalDateTime toDate,
                               Long campaignId,
                               String eventType,
                               CampaignChannel channel,
                               String utmSource,
                               String utmMedium,
                               String country) {
        validateSiteOwnership(siteCode, user);
        return analyticsRepository.countBySiteWithFilterInDateRange(
                siteCode, fromDate, toDate, campaignId, eventType, channel, utmSource,
                utmMedium, country);
    }


    @Transactional(readOnly = true)
    public List<DailyCount> getTimeseries(String siteCode,
                                          User user,
                                          LocalDateTime fromDate,
                                          LocalDateTime toDate,
                                          Long campaignId,
                                          String eventType,
                                          CampaignChannel channel,
                                          String utmSource,
                                          String utmMedium,
                                          String country) {
        validateSiteOwnership(siteCode, user);
        return analyticsRepository.findBySiteWithFilterInDateRange(
                siteCode, fromDate, toDate, campaignId, eventType, channel, utmSource,
                utmMedium, country);
    }
}
