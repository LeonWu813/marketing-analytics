package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.SiteResponse;
import com.leon.marketing_analytics.dto.CreateSiteRequest;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private SiteResponse toResponse(Site site) {
        return new SiteResponse(site.getId(), site.getSiteCode(), site.getSiteName());
    }

    @Transactional
    public SiteResponse createSite(CreateSiteRequest request, User user) {
        Site site = Site.builder()
                .siteCode(UUID.randomUUID().toString())
                .siteName(request.siteName())
                .user(user)
                .build();

        Site saved = siteRepository.save(site);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SiteResponse> getSites(User user) {
        return siteRepository.findAllByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}