package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.ReportSendLog;
import com.leon.marketing_analytics.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportSendRepository extends JpaRepository<ReportSendLog, Long> {

    @Query("SELECT l FROM ReportSendLog l "+
            "WHERE l.report.site = :site "+
            "ORDER BY l.sentAt DESC"
    )
    List<ReportSendLog> findBySite(@Param("site") Site site);

    List<ReportSendLog> findByReportId(Long id);

}
