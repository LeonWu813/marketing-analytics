package com.leon.marketing_analytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.marketing_analytics.dto.PageSpeedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PageSpeedService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    @Value("${pagespeed.api.key:}")
    private String key;

    public PageSpeedService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public PageSpeedResult analyze(String url) {
        Map<String, Object> rawResponse = null;
        try {
            String json = restClient.get()
                    .uri("https://www.googleapis.com/pagespeedonline/v5/runPagespeed" +
                            "?url={url}&strategy=mobile&key={key}", url, key)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(json);
            rawResponse = objectMapper.convertValue(root, new TypeReference<Map<String, Object>>() {
            });
            int performanceScore = getScore(root, "performance");
            int seoScore = getScore(root, "seo");
            String lcpScore = getMetricsWithUnit(root, "largest-contentful-paint");
            String fcpScore = getMetricsWithUnit(root, "first-contentful-paint");
            String tbtScore = getMetricsWithUnit(root, "total-blocking-time");
            List<String> warnings = getWarning(root);

            return new PageSpeedResult(rawResponse, performanceScore, seoScore, lcpScore, fcpScore, tbtScore, warnings);
        } catch (Exception e) {
            return failedResult();
        }
    }

    private int getScore(JsonNode root, String category) {
        JsonNode scoreNode = root
                .path("lighthouseResult")
                .path("categories")
                .path(category)
                .path("score");
        if (scoreNode.isMissingNode() || scoreNode.isNull()) {
            return -1;   // -1 = unavailable, distinct from a genuine 0 score
        }
        return (int) (scoreNode.asDouble() * 100);
    }

    private String getMetricsWithUnit(JsonNode root, String metric) {
        JsonNode metricRoot = root
                .path("lighthouseResult")
                .path("audits")
                .path(metric);
        if (metricRoot.isMissingNode()) return "N/A";

        double value = metricRoot.path("numericValue").asDouble();
        String unit  = metricRoot.path("numericUnit").asText();

        return String.format("%.1f %s", value, unit);
    }

    private List<String> getWarning(JsonNode root){
        JsonNode runWarnings = root.path("lighthouseResult").path("runWarnings");
        List<String> warningsList = new ArrayList<>();
        if (runWarnings.isArray()) {
            for (JsonNode warningNode : runWarnings) {
                warningsList.add(warningNode.asText());
            }
        }
        return warningsList;
    }

    private PageSpeedResult failedResult() {
        return new PageSpeedResult(null, -1, -1,
                "N/A", "N/A", "N/A", List.of());
    }
}
