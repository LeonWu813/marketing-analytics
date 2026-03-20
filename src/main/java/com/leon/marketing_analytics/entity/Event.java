package com.leon.marketing_analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "events",
        indexes = {
                @Index(name = "idx_events_site_created", columnList = "site_id, created_at DESC"),
                @Index(name = "idx_events_site_type", columnList = "site_id, event_type"),
                @Index(name = "idx_events_channel", columnList = "site_id, channel"),
                @Index(name = "idx_events_country", columnList = "site_id, country"),

        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 500)
    private String pageUrl;

    @Column(length = 100)
    private String utmSource;

    @Column(length = 100)
    private String utmMedium;

    @Column(length = 100)
    private String utmCampaign;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private CampaignChannel channel;

    private String userIdentifier;

    @Column(length = 100)
    private String country;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
