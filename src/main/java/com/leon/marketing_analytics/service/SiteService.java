package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.SiteResponse;
import com.leon.marketing_analytics.dto.SiteResquest;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private SiteResponse toResponse(Site site) {
        return new SiteResponse(site.getId(), site.getSiteCode(), site.getSiteName(), site.getUser());
    }

    @Transactional
    public SiteResponse creatSite(SiteResquest request, User user) {
        Site site = Site.builder()
                .siteCode(UUID.randomUUID().toString())
                .siteName(request.name())
                .user(user)
                .build();

        return toResponse(site);
    }
}