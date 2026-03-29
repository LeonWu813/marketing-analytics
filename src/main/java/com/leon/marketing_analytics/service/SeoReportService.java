package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.PageSpeedResult;
import com.leon.marketing_analytics.dto.SeoCheckResult;
import com.leon.marketing_analytics.dto.SeoReportResponse;
import com.leon.marketing_analytics.entity.SeoCheck;
import com.leon.marketing_analytics.entity.SeoReport;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.repository.SeoReportRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoReportService {

    private final SeoReportRepository seoReportRepository;
    private final SiteRepository siteRepository;
    private final PageSpeedService pageSpeedService;
    private final JsoupScraperService jsoupScraperService;

    private SeoReportResponse toResponse(SeoReport report) {
        return new SeoReportResponse(
                report.getId(),
                report.getSite().getSiteCode(),
                report.getAnalyzedUrl(),
                report.getKeyword(),
                report.getAnalyzedAt(),
                report.getChecks().stream()
                        .map(c -> new SeoCheckResult(c.getCheckName(), c.getCheckStatus(), c.getDetails()))
                        .toList(),
                report.getPerformanceScore(),
                report.getSeoScore(),
                report.getLcpSeconds(),
                report.getFcpSeconds(),
                report.getTbtMilliseconds(),
                report.getRunWarnings()
        );
    }

    private SeoCheck seoCheckResultToSeoCheck(SeoCheckResult result, SeoReport seoReport) {
        return SeoCheck.builder()
                .seoReport(seoReport)
                .checkName(result.checkName())
                .checkStatus(result.checkStatus())
                .details(result.details())
                .build();
    }

    private Site checkSiteOwnership(String siteCode, User user) {
        return siteRepository.findBySiteCodeAndUser(siteCode, user).orElseThrow(() ->
                new ForbiddenException("No access to the site")
        );
    }

    @Transactional
    public SeoReport buildAndSaveReport(Site site, String analyzedUrl, String keyword, boolean isFollowUpEmail) {
        SeoReport report = SeoReport.builder()
                .site(site)
                .analyzedUrl(analyzedUrl)
                .keyword(keyword)
                .followUpCompleted(isFollowUpEmail)
                .followUpAt(!isFollowUpEmail ? LocalDateTime.now().plusDays(7) : null)
                .build();

        List<SeoCheckResult> jsoupScraper = jsoupScraperService.scrapeUrl(analyzedUrl, keyword);
        List<SeoCheck> checks = jsoupScraper.stream()
                .map(c -> seoCheckResultToSeoCheck(c, report)).toList();
        PageSpeedResult pageSpeedInsight = pageSpeedService.analyze(analyzedUrl);

        report.setChecks(checks);
        report.setPerformanceScore(pageSpeedInsight.performanceScore());
        report.setSeoScore(pageSpeedInsight.seoScore());
        report.setLcpSeconds(pageSpeedInsight.lcpSeconds());
        report.setFcpSeconds(pageSpeedInsight.fcpSeconds());
        report.setTbtMilliseconds(pageSpeedInsight.tbtMilliseconds());
        report.setRunWarnings(pageSpeedInsight.warnings());
        report.setPagespeedRaw(pageSpeedInsight.rawResponse());

        seoReportRepository.save(report);
        return report;
    }

    @Transactional
    public SeoReportResponse analyze(String analyzedUrl, String keyword, String siteCode, User user) {
        Site site = checkSiteOwnership(siteCode, user);
        SeoReport report = buildAndSaveReport(site, analyzedUrl, keyword, false);
        return toResponse(report);
    }


    @Transactional(readOnly = true)
    public Page<SeoReportResponse> getReports(String siteCode, String analyzedUrl, Pageable pageable, User user) {
        checkSiteOwnership(siteCode, user);

        Page<SeoReport> reports = seoReportRepository.findBySiteCodeWithFilters(siteCode, analyzedUrl, pageable);

        return reports.map(this::toResponse);
    }


}
