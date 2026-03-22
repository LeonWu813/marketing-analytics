package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.site.id = :siteId " +
            "AND (:campaignId IS NULL OR e.campaign.id = :campaignId) " +
            "AND (:eventType IS NULL OR e.eventType = :eventType) " +
            "AND (:channel IS NULL OR e.channel = :channel) " +
            "AND (:startDate IS NULL OR e.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR e.createdAt <= :endDate) " +
            "ORDER BY e.createdAt DESC")
    Page<Event> findBySiteWithFilters(
            @Param("siteId") Long siteId,
            @Param("campaignId") Long campaignId,
            @Param("eventType") String eventType,
            @Param("channel") CampaignChannel channel,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
