package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.ReportSendRequest;
import com.leon.marketing_analytics.dto.ReportSendResponse;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.ReportSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{site_code}/seo/reports")
@RequiredArgsConstructor
public class ReportSendController {
    private final ReportSendService reportSendService;

    @PostMapping("/{report_id}/share")
    public ResponseEntity<ReportSendResponse> sendReport(
            @PathVariable String site_code,
            @PathVariable Long report_id,
            @Valid @RequestBody ReportSendRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                reportSendService.reportSendRequest(site_code, report_id, request, user)
        );
    }

    @GetMapping("/shares")
    public ResponseEntity<List<ReportSendResponse>> getReports(
            @PathVariable String site_code,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(reportSendService.getSentHistory(site_code, user));
    }
}
