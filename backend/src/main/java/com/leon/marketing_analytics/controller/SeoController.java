package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.SeoReportRequest;
import com.leon.marketing_analytics.dto.SeoReportResponse;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.SeoReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{site_code}/seo")
@RequiredArgsConstructor
public class SeoController {

    private final SeoReportService seoReportService;

    @PostMapping("/analyze")
    public ResponseEntity<SeoReportResponse> analyze(
            @Valid @RequestBody SeoReportRequest request,
            @PathVariable String site_code,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seoReportService.analyze(
                        request.analyzedUrl(), request.keyword(), site_code, currentUser));
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<SeoReportResponse>> get(
            @PathVariable String site_code,
            @RequestParam(required = false) String analyzedUrl,
            @PageableDefault(size = 20, sort = "analyzedAt") Pageable pageable,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(seoReportService.getReports(site_code, analyzedUrl, pageable, currentUser));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<SeoReportResponse> getSingle(
            @PathVariable String site_code,
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(seoReportService.getReport(site_code, id, currentUser));
    }
}
