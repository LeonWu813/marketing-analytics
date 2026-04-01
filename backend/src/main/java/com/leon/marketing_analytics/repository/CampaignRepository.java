package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.user = :user
            AND c.site = :site
            AND c.isArchived = false
            AND (:status IS NULL OR c.status = :status)
            AND (:channel IS NULL OR c.channel = :channel)
            ORDER BY c.createdAt DESC
            """)
    Page<Campaign> findByUserAndSiteWithFilters(
            @Param("user") User user,
            @Param("site") Site site,
            @Param("status") CampaignStatus status,
            @Param("channel") CampaignChannel channel,
            Pageable pageable
    );

    boolean existsByIdAndUser(Long id, User user);
}
