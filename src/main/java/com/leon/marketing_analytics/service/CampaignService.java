package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.CampaignResponse;
import com.leon.marketing_analytics.dto.CreateCampaignRequest;
import com.leon.marketing_analytics.dto.UpdateCampaignRequest;
import com.leon.marketing_analytics.entity.*;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.exception.ResourceNotFoundException;
import com.leon.marketing_analytics.repository.CampaignRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final SiteRepository siteRepository;

    private Site getSite(Long site_id, User user) {
        return siteRepository.findByIdAndUser(site_id, user)
                .orElseThrow(() -> new ResourceNotFoundException("The campaign doesn't exist in this site."));
    }

    private CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(campaign.getId(),
                campaign.getCreatedAt(),
                campaign.getCampaignName(),
                campaign.getCampaignDescription(),
                campaign.getCost(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getStatus(),
                campaign.getChannel(),
                campaign.getMetricName(),
                campaign.getMetricValue(),
                campaign.getBenchmarkMetricName(),
                campaign.getBenchmarkMetricValue(),
                campaign.isArchived());
    }

    private Campaign findCampaignAndValidateOwnership(Long id, Long site_id, User user) {
        Campaign searched = campaignRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        if (!searched.getSite().getId().equals(site_id)) {
            throw new ForbiddenException("Access forbidden");
        }

        return searched;
    }

    @Transactional
    public CampaignResponse createCampaign(CreateCampaignRequest request, Long site_id, User user) {
        Campaign campaign = Campaign.builder()
                .campaignName(request.campaignName())
                .site(getSite(site_id, user))
                .campaignDescription(request.campaignDescription())
                .cost(request.cost())
                .channel(request.channel())
                .user(user)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .metricName(request.metricName())
                .metricValue(request.metricValue())
                .benchmarkMetricName(request.benchmarkMetricName())
                .benchmarkMetricValue(request.benchmarkMetricValue())
                .build();

        Campaign created = campaignRepository.save(campaign);
        return toResponse(created);
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaignById(Long id, Long site_id, User user) {
        Campaign searched = findCampaignAndValidateOwnership(id, site_id, user);

        return toResponse(searched);
    }

    @Transactional(readOnly = true)
    public Page<CampaignResponse> getCampaigns(
            User user, Long site_id, CampaignStatus status, CampaignChannel channel, Pageable pageable) {
        Page<Campaign> searchedCampaigns;
        Site site = getSite(site_id, user);
        if (status == null && channel == null) {
            searchedCampaigns = campaignRepository
                    .findAllByUserAndSiteAndIsArchivedFalse(user, site, pageable);
        } else if (status == null) {
            searchedCampaigns = campaignRepository
                    .findAllByUserAndSiteAndChannelAndIsArchivedFalse(user, site, channel, pageable);
        } else if (channel == null) {
            searchedCampaigns = campaignRepository
                    .findAllByUserAndSiteAndStatusAndIsArchivedFalse(user, site, status, pageable);
        } else {
            searchedCampaigns = campaignRepository
                    .findAllByUserAndSiteAndStatusAndChannelAndIsArchivedFalse(user, site, status, channel, pageable);
        }

        return searchedCampaigns.map(this::toResponse);
    }

    @Transactional
    public CampaignResponse updateCampaign(
            Long id, Long site_id, UpdateCampaignRequest updateCampaignRequest, User user) {

        Campaign searched = findCampaignAndValidateOwnership(id, site_id, user);

        searched.setCampaignName(updateCampaignRequest.campaignName());
        searched.setCampaignDescription(updateCampaignRequest.campaignDescription());
        searched.setCost(updateCampaignRequest.cost());
        searched.setStartDate(updateCampaignRequest.startDate());
        searched.setEndDate(updateCampaignRequest.endDate());
        searched.setStatus(updateCampaignRequest.status());
        searched.setChannel(updateCampaignRequest.channel());
        searched.setMetricName(updateCampaignRequest.metricName());
        searched.setMetricValue(updateCampaignRequest.metricValue());
        searched.setBenchmarkMetricName(updateCampaignRequest.benchmarkMetricName());
        searched.setBenchmarkMetricValue(updateCampaignRequest.benchmarkMetricValue());


        Campaign updated = campaignRepository.save(searched);

        return toResponse(updated);
    }

    @Transactional
    public void deleteCampaign(Long id, Long site_id, User user) {

        Campaign searched = findCampaignAndValidateOwnership(id, site_id, user);

        searched.setStatus(CampaignStatus.END);
        searched.setArchived(true);

        campaignRepository.save(searched);
    }
}
