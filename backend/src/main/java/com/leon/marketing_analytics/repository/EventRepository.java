package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.Event;
import com.leon.marketing_analytics.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT e.* FROM events e " +
            "JOIN sites s ON s.id = e.site_id " +
            "WHERE s.site_code = :siteCode " +
            "AND (CAST(:eventType AS text) IS NULL OR e.event_type = CAST(:eventType AS text)) " +
            "AND (CAST(:channel AS text) IS NULL OR e.channel = CAST(:channel AS text)) " +
            "AND (CAST(:utmSource AS text) IS NULL OR e.utm_source = CAST(:utmSource AS text)) " +
            "AND (CAST(:utmMedium AS text) IS NULL OR e.utm_medium = CAST(:utmMedium AS text)) " +
            "AND (CAST(:country AS text) IS NULL OR e.country = CAST(:country AS text)) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR e.created_at >= CAST(:startDate AS timestamp)) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR e.created_at <= CAST(:endDate AS timestamp)) " +
            "ORDER BY e.created_at DESC", nativeQuery = true)
    ArrayList<Event> findBySiteWithFilters(
            @Param("siteCode") String siteCode,
            @Param("eventType") String eventType,
            @Param("channel") CampaignChannel channel,
            @Param("utmSource") String utmSource,
            @Param("utmMedium") String utmMedium,
            @Param("country") String country,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Page<Event> findBySite(Site site, Pageable pageable);
}
