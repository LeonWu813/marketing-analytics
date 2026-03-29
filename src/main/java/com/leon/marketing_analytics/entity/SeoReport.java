package com.leon.marketing_analytics.entity;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "seo_reports",
        indexes = {
                @Index(name = "idx_seo_reports_site_id", columnList = "site_id"),
                @Index(name = "idx_seo_reports_follow_up", columnList = "follow_up_completed, follow_up_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false)
    private String analyzedUrl;

    private String keyword;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "follow_up_at")
    private LocalDateTime followUpAt;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean followUpCompleted = false;

    @Builder.Default
    @OneToMany(mappedBy = "seoReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeoCheck> checks = new ArrayList<>();

    @Column
    private int performanceScore;

    @Column
    private int seoScore;

    @Column
    private String lcpSeconds;

    @Column
    private String fcpSeconds;

    @Column
    private String tbtMilliseconds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> runWarnings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pagespeed_raw", columnDefinition = "jsonb")
    private Map<String, Object> pagespeedRaw;

}
