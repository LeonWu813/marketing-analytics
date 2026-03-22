package com.leon.marketing_analytics.repository;

import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByIdAndUser(Long id, User user);

    Optional<Site> findBySiteCode(String siteCode);

    List<Site> findAllByUser(User user);
}
