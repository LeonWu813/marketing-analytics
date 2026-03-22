package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.EventIngestRequest;
import com.leon.marketing_analytics.dto.EventResponse;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventservice;

    @PostMapping
    public ResponseEntity<EventResponse> ingest(
            @Valid @RequestBody EventIngestRequest request,
            HttpServletRequest httpRequest){
        String ipAddress = httpRequest.getRemoteAddr();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventservice.createEvent(request, ipAddress));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam String siteCode,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) CampaignChannel channel,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(eventservice
                .getEvents(siteCode, campaignId, eventType,channel,startDate,endDate,pageable,currentUser));
    }
}
