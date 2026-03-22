package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.CreateSiteRequest;
import com.leon.marketing_analytics.dto.SiteResponse;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;

    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody CreateSiteRequest createSiteRequest,
                                                @AuthenticationPrincipal User currentUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(siteService.createSite(createSiteRequest, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<SiteResponse>> getSites(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(siteService.getSites(currentUser));
    }
}
