package com.leon.marketing_analytics.controller;

import com.leon.marketing_analytics.dto.CampaignResponse;
import com.leon.marketing_analytics.dto.CreateCampaignRequest;
import com.leon.marketing_analytics.dto.UpdateCampaignRequest;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.CampaignStatus;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.service.CampaignService;
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
@RequestMapping("/api/{site_code}/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CreateCampaignRequest request,
                                                   @PathVariable String site_code,
                                                   @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.createCampaign(request, site_code, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> campaigns(@AuthenticationPrincipal User currentUser,
                                                            @PathVariable String site_code,
                                                            @RequestParam(required = false) CampaignStatus status,
                                                            @RequestParam(required = false) CampaignChannel channel,
                                                            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaigns(currentUser, site_code, status, channel, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> get(@PathVariable Long id,
                                                @PathVariable String site_code,
                                                @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(campaignService.getCampaignById(id, site_code, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> update(@PathVariable Long id,
                                                   @PathVariable String site_code,
                                                   @Valid @RequestBody UpdateCampaignRequest updateCampaignRequest,
                                                   @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, site_code, updateCampaignRequest, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @PathVariable String site_code,
                                       @AuthenticationPrincipal User currentUser) {
        campaignService.deleteCampaign(id, site_code, currentUser);
        return ResponseEntity.noContent().build();
    }
}
