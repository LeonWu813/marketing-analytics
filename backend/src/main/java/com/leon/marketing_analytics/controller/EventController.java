package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.EventIngestRequest;
import com.leon.marketing_analytics.dto.EventResponse;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventservice;

    @PostMapping
    public ResponseEntity<EventResponse> ingest(
            @Valid @RequestBody EventIngestRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventservice.createEvent(request, ipAddress));
    }

    @GetMapping
    public ResponseEntity<ArrayList<EventResponse>> getEvents(
            @RequestParam String siteCode,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) CampaignChannel channel,
            @RequestParam(required = false) String utmSource,
            @RequestParam(required = false) String utmMedium,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventservice
                .getEvents(siteCode, eventType, channel,
                        utmSource, utmMedium, country, startDate, endDate, currentUser));
    }
}
