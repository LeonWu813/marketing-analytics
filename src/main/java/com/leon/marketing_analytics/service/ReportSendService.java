package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.ReportSendRequest;
import com.leon.marketing_analytics.dto.ReportSendResponse;
import com.leon.marketing_analytics.entity.ReportSendLog;
import com.leon.marketing_analytics.entity.SeoReport;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.exception.ResourceNotFoundException;
import com.leon.marketing_analytics.repository.ReportSendRepository;
import com.leon.marketing_analytics.repository.SeoReportRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportSendService {

    private final EmailService emailService;
    private final SeoReportService seoReportService;
    private final SeoReportRepository seoReportRepository;
    private final ReportSendRepository reportSendRepository;
    private final SiteRepository siteRepository;

    private ReportSendResponse toResponse(ReportSendLog log) {
        return new ReportSendResponse(log.getId(), log.getSentTo(), log.getSentAt(), log.getReport().getId());
    }

    private String addHtmlTag(String body) {
        return "<html><body style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.5; margin: 0; " +
                "padding: 0;\">" + body + "</body></html>";
    }

    private String getReportHtmlEmail(SeoReport report) {
        String reportAt = report.getAnalyzedAt().toLocalDate().toString();
        String analyzedUrl = report.getAnalyzedUrl();
        String keyword = report.getKeyword();
        int performanceScore = report.getPerformanceScore();
        int seoScore = report.getSeoScore();
        String checks = report.getChecks().toString();
        String lcpSeconds = report.getLcpSeconds();
        String fcpSeconds = report.getFcpSeconds();
        String tbtMilliseconds = report.getTbtMilliseconds();
        String runWarnings = report.getRunWarnings() != null
                ? report.getRunWarnings().toString()
                : "None";

        String body = """
                <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                  <tr>
                    <td align="center">
                      <!-- Main content container table -->
                      <table width="600" cellpadding="0" cellspacing="0" border="0" style="border: 1px solid #e0e0e0; border-radius: 8px; margin: 20px auto;">
                        <tr>
                          <td style="padding: 20px;">
                            <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px; text-align: center;">SEO Report</h1>
                            <p>%s report at %s</p>
                
                            <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 20px; border-collapse: collapse;">
                              <tr>
                                <td style="padding: 5px; border: 1px solid #ddd;">Keyword: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Performance Score: %d</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">SEO Score: %d</td>
                              </tr>
                              <tr>
                                <td style="padding: 5px; border: 1px solid #ddd;">SEO checks:</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">%s</td>
                              </tr>
                              <tr>
                                <td style="padding: 5px; border: 1px solid #ddd;">Google Pagespeed Insight:</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Largest Contentful Paint: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">First Contentful Paint: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Total Blocking Time: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Warnings: %s</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                """;

        return String.format(body, analyzedUrl, reportAt, keyword, performanceScore, seoScore,
                checks, lcpSeconds, fcpSeconds, tbtMilliseconds, runWarnings);
    }

    private String getChanges(SeoReport report, SeoReport previous) {
        int performanceScore = report.getPerformanceScore();
        int performanceScorePre = previous.getPerformanceScore();
        int seoScore = report.getSeoScore();
        int seoScorePre = previous.getSeoScore();
        int checks = report.getChecks().size();
        int checksPre = previous.getChecks().size();
        String lcpSeconds = report.getLcpSeconds();
        String lcpSecondsPre = previous.getLcpSeconds();
        String fcpSeconds = report.getFcpSeconds();
        String fcpSecondsPre = previous.getFcpSeconds();
        String tbtMilliseconds = report.getTbtMilliseconds();
        String tbtMillisecondsPre = previous.getTbtMilliseconds();
        int runWarnings = report.getRunWarnings() != null ? report.getRunWarnings().size() : 0;
        int runWarningsPre = previous.getRunWarnings() != null ? previous.getRunWarnings().size() : 0;
        String body = """
                <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                  <tr>
                    <td align="center">
                      <!-- Main content container table -->
                      <table width="600" cellpadding="0" cellspacing="0" border="0" style="border: 1px solid #e0e0e0; border-radius: 8px; margin: 20px auto;">
                        <tr>
                          <td style="padding: 20px;">
                            <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px; text-align: center;">Changes</h1>
                            <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 20px; border-collapse: collapse;">
                              <tr>
                                <td style="padding: 5px; border: 1px solid #ddd;">Seo Improvements Checks: Original improvements: %d   →   New improvements: %d</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Performance Score: Original score: %d   →   New score: %d</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Seo Score: Original score: %d   →   New score: %d</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Largest Contentful Paint: Original score: %s   →   New score: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">First Contentful Paint: Original score: %s   →   New score: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Total Blocking Time: Original score: %s   →   New score: %s</td>
                                <td style="padding: 5px; border: 1px solid #ddd;">Run Warnings: Original score: %d   →   New score: %d</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                """;

        return String.format(body,
                checksPre, checks,              // SEO Improvements row
                performanceScorePre, performanceScore,  // Performance Score row
                seoScorePre, seoScore,          // SEO Score row
                lcpSecondsPre, lcpSeconds,      // LCP row
                fcpSecondsPre, fcpSeconds,      // FCP row
                tbtMillisecondsPre, tbtMilliseconds,    // TBT row
                runWarningsPre, runWarnings);   // Run Warnings row
    }

    private String getFollowUpReportHtmlEmail(SeoReport report, SeoReport previous) {
        return addHtmlTag(getChanges(report, previous) + getReportHtmlEmail(report));
    }

    private Site checkSiteOwnership(String siteCode, User user) {
        return siteRepository.findBySiteCodeAndUser(siteCode, user).orElseThrow(() ->
                new ForbiddenException("Access forbidden"));
    }

    private ReportSendResponse sendEmailAndLog(SeoReport report, String sentTo, String subject, String htmlEmail,
                                               User user) {
        emailService.sendHtmlEmail(sentTo, subject, htmlEmail);

        ReportSendLog reportSendLog = ReportSendLog.builder()
                .user(user)
                .report(report)
                .sentTo(sentTo)
                .build();
        reportSendRepository.save(reportSendLog);

        return toResponse(reportSendLog);
    }

    private void sendFollowUpEmail(SeoReport report) {
        SeoReport followUpReport = seoReportService.buildAndSaveReport(
                report.getSite(), report.getAnalyzedUrl(), report.getKeyword(), true);
        String subject = "[Website Follow up Report] On " + report.getAnalyzedUrl();
        String followUpHtml = getFollowUpReportHtmlEmail(followUpReport, report);

        List<ReportSendLog> history = reportSendRepository.findByReportId(report.getId());
        String recipient = history.isEmpty()
                ? report.getSite().getUser().getEmail()
                : history.getFirst().getSentTo();
        User logUser = history.isEmpty()
                ? report.getSite().getUser()
                : history.getFirst().getUser();

        sendEmailAndLog(followUpReport, recipient, subject, followUpHtml, logUser);
        report.setFollowUpCompleted(true);
        seoReportRepository.save(report);
    }

    @Transactional
    public ReportSendResponse reportSendRequest(String siteCode, Long reportId, ReportSendRequest request, User user) {
        Site site = checkSiteOwnership(siteCode, user);
        SeoReport report = seoReportRepository.findSeoReportByIdAndSite(reportId, site).orElseThrow(() ->
                new ResourceNotFoundException("Report not found"));

        String htmlEmail = addHtmlTag(getReportHtmlEmail(report));
        String subject = "[Website Report] On " + report.getAnalyzedUrl();
        return sendEmailAndLog(report, request.sentTo(), subject, htmlEmail, user);
    }

    @Transactional(readOnly = true)
    public List<ReportSendResponse> getSentHistory(String siteCode, User user) {
        Site site = checkSiteOwnership(siteCode, user);
        List<ReportSendLog> reports = reportSendRepository.findBySite(site);

        return reports.stream().map(this::toResponse).toList();
    }


    @Transactional
    public void processFollowUps() {
        List<SeoReport> dueReports = seoReportRepository.findDueFollowUps(LocalDateTime.now());
        for (SeoReport report : dueReports) {
            try {
                sendFollowUpEmail(report);
            } catch (Exception e) {
                log.error("Follow-up failed for report {}: {}", report.getId(), e.getMessage());
            }
        }
    }
}
