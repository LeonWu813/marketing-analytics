package com.leon.marketing_analytics.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String campaignName;

    @Column
    private String campaignDescription;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignChannel channel;

    @Column
    private String metricName;

    @Column
    private BigDecimal metricValue;

    @Column
    private String benchmarkMetricName;

    @Column
    private BigDecimal benchmarkMetricValue;

    @Builder.Default
    @Column(nullable = false)
    private boolean isArchived = false;
}
