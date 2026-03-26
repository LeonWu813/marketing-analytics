package com.leon.marketing_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seo_checks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seo_report_id", nullable = false)
    private SeoReport seoReport;

    @Column(nullable = false)
    private String checkName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckStatus checkStatus;

    @Column(columnDefinition = "TEXT")
    private String details;

}
