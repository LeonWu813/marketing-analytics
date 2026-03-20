package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findAllByUserAndSiteAndIsArchivedFalse(User user, Site site, Pageable pageable);

    Page<Campaign> findAllByUserAndSiteAndStatusAndIsArchivedFalse(User user, Site site, CampaignStatus status, Pageable pageable);

    Page<Campaign> findAllByUserAndSiteAndChannelAndIsArchivedFalse(User user, Site site, CampaignChannel channel, Pageable pageable);

    Page<Campaign> findAllByUserAndSiteAndStatusAndChannelAndIsArchivedFalse(
            User user, Site site, CampaignStatus status, CampaignChannel channel, Pageable pageable);

    boolean existsByIdAndUser(Long id, User user);
}
