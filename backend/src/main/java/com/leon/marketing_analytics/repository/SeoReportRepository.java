package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.SeoReport;
import com.leon.marketing_analytics.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeoReportRepository extends JpaRepository<SeoReport, Long> {

    @Query("SELECT r FROM SeoReport r " +
            "WHERE r.site.siteCode = :siteCode " +
            "AND (:analyzedUrl IS NULL OR r.analyzedUrl = :analyzedUrl)" +
            "ORDER BY r.analyzedAt DESC"
    )
    Page<SeoReport> findBySiteCodeWithFilters(
            @Param("siteCode") String siteCode,
            @Param("analyzedUrl") String analyzedUrl,
            Pageable pageable
    );

    @Query("SELECT r FROM SeoReport r " +
            "JOIN FETCH r.checks " +
            "WHERE r.followUpCompleted = false " +
            "AND r.followUpAt < :now"
    )
    List<SeoReport> findDueFollowUps(@Param("now") LocalDateTime now);

    Optional<SeoReport> findSeoReportByIdAndSite(Long reportId, Site site);
}
