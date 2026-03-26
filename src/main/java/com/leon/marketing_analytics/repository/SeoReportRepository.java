package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.SeoReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeoReportRepository extends JpaRepository<SeoReport, Long> {

    @Query("SELECT r FROM SeoReport r " +
            "WHERE r.site.siteCode = :siteCode " +
            "AND (:analyzedUrl IS NULL OR r.analyzedUrl = :analyzedUrl) " +
            "ORDER BY r.analyzedAt DESC"
    )
    Page<SeoReport> findBySiteCodeWithFilters(
            @Param("siteCode") String siteCode,
            @Param("analyzedUrl") String analyzedUrl,
            Pageable pageable
            );
}
