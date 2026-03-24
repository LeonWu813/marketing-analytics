package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.DailyCount;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/{site_code}/events_count")
    public ResponseEntity<Long> getEvents(
            @PathVariable String site_code,
            @AuthenticationPrincipal User currentUser,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) CampaignChannel channel,
            @RequestParam(required = false) String utmSource,
            @RequestParam(required = false) String utmMedium,
            @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(analyticsService
                .getTotalEvents(site_code, currentUser, fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX),
                        campaignId, eventType, channel, utmSource, utmMedium, country));
    }

    @GetMapping("/{site_code}/event_time_series")
    public ResponseEntity<List<DailyCount>> getEventTimeSeries(
            @PathVariable String site_code,
            @AuthenticationPrincipal User currentUser,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) CampaignChannel channel,
            @RequestParam(required = false) String utmSource,
            @RequestParam(required = false) String utmMedium,
            @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(analyticsService
                .getTimeseries(site_code, currentUser, fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX),
                        campaignId, eventType, channel, utmSource, utmMedium, country));
    }
}