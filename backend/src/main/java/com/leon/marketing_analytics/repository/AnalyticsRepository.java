package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.dto.DailyCount;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Event, Long> {

    @Query("SELECT COUNT(e) FROM Event e " +
            "WHERE e.site.siteCode = :siteCode " +
            "AND e.createdAt >= :fromDate " +
            "AND e.createdAt <= :toDate " +
            "AND (:campaignId IS NULL OR e.campaign.id = :campaignId) " +
            "AND (:eventType IS NULL OR e.eventType = :eventType) " +
            "AND (:channel IS NULL OR e.channel = :channel) " +
            "AND (:utmSource IS NULL OR e.utmSource = :utmSource) " +
            "AND (:utmMedium IS NULL OR e.utmMedium = :utmMedium) " +
            "AND (:country IS NULL OR e.country = :country) "
    )
    Long countBySiteWithFilterInDateRange(
            @Param("siteCode") String siteCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("campaignId") Long campaignId,
            @Param("eventType") String eventType,
            @Param("channel") CampaignChannel channel,
            @Param("utmSource") String utmSource,
            @Param("utmMedium") String utmMedium,
            @Param("country") String country
    );

    @Query("SELECT new com.leon.marketing_analytics.dto.DailyCount(CAST(e.createdAt AS LocalDate), COUNT(e)) " +
            "FROM Event e " +
            "WHERE e.site.siteCode = :siteCode " +
            "AND e.createdAt >= :fromDate " +
            "AND e.createdAt <= :toDate " +
            "AND (:campaignId IS NULL OR e.campaign.id = :campaignId) " +
            "AND (:eventType IS NULL OR e.eventType = :eventType) " +
            "AND (:channel IS NULL OR e.channel = :channel) " +
            "AND (:utmSource IS NULL OR e.utmSource = :utmSource) " +
            "AND (:utmMedium IS NULL OR e.utmMedium = :utmMedium) " +
            "AND (:country IS NULL OR e.country = :country) " +
            "GROUP BY CAST(e.createdAt AS LocalDate) " +
            "ORDER BY CAST(e.createdAt AS LocalDate) ASC"
    )
    List<DailyCount> findBySiteWithFilterInDateRange(
            @Param("siteCode") String siteCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("campaignId") Long campaignId,
            @Param("eventType") String eventType,
            @Param("channel") CampaignChannel channel,
            @Param("utmSource") String utmSource,
            @Param("utmMedium") String utmMedium,
            @Param("country") String country

    );
}