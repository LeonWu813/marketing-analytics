package com.leon.marketing_analytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.marketing_analytics.dto.PageSpeedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                            "?url={url}&strategy=mobile&key={key}&category=performance&category=seo", url, key)
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
            String loadingExperience = getLoadingExperience(root);
            List<Map<String, Object>> seoAudits = getSeoAudits(root);
            List<Map<String, Object>> opportunities = getOpportunities(root);
            List<String> warnings = getWarning(root);

            return new PageSpeedResult(rawResponse, performanceScore, seoScore, lcpScore, fcpScore, tbtScore,
                    loadingExperience, seoAudits, opportunities, warnings);
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
        String unitFull = metricRoot.path("numericUnit").asText();
        String unit = !unitFull.equals("millisecond") ? (unitFull.equals("second") ? "s" : unitFull) : "ms";

        return String.format("%.1f %s", value, unit);
    }

    private List<String> getWarning(JsonNode root) {
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
                "N/A", "N/A", "N/A", "N/A",
                null, null, List.of());
    }

    private String getLoadingExperience(JsonNode root) {
        JsonNode loadingExperience = root
                .path("loadingExperience")
                .path("overall_category");
        if (loadingExperience.isMissingNode()) {
            return "N/A";
        } else {
            String res = loadingExperience.toString();
            return res.substring(1, res.length() - 1).toLowerCase();
        }
    }

    private List<Map<String, Object>> getSeoAudits(JsonNode root) {
        List<Map<String, Object>> results = new ArrayList<>();

        JsonNode auditRefs = root
                .path("lighthouseResult")
                .path("categories")
                .path("seo")
                .path("auditRefs");

        if (!auditRefs.isArray()) return results;

        for (JsonNode ref : auditRefs) {
            String auditId = ref.path("id").asText();
            JsonNode audit = root
                    .path("lighthouseResult")
                    .path("audits")
                    .path(auditId);

            if (audit.isMissingNode()) continue;

            double score = audit.path("score").asDouble(-1);
            String title = audit.path("title").asText("N/A");
            String displayValue = audit.path("displayValue").asText("");
            String scoreDisplayMode = audit.path("scoreDisplayMode").asText("");

            if (scoreDisplayMode.equals("notApplicable") ||
                    scoreDisplayMode.equals("informative")) continue;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", auditId);
            entry.put("title", title);
            entry.put("passed", score == 1.0);
            entry.put("score", score);
            entry.put("displayValue", displayValue);

            results.add(entry);
        }
        return results;
    }

    private List<Map<String, Object>> getOpportunities(JsonNode root) {
        List<Map<String, Object>> results = new ArrayList<>();

        JsonNode audits = root.path("lighthouseResult").path("audits");

        audits.fields().forEachRemaining(entry -> {
            JsonNode audit = entry.getValue();
            JsonNode details = audit.path("details");

            String detailsType = details.path("type").asText("");
            String scoreDisplayMode = audit.path("scoreDisplayMode").asText("");

            // skip non-actionable modes
            if (scoreDisplayMode.equals("notApplicable") ||
                    scoreDisplayMode.equals("informative") ||
                    scoreDisplayMode.equals("manual")) return;

            // skip passing audits (score >= 0.9)
            JsonNode scoreNode = audit.path("score");
            if (scoreNode.isMissingNode() || scoreNode.isNull()) return;
            double score = scoreNode.asDouble(1.0);
            if (score >= 0.9) return;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", entry.getKey());
            item.put("title", audit.path("title").asText());
            item.put("displayValue", audit.path("displayValue").asText(""));
            item.put("score", score);
            item.put("description", audit.path("description").asText(""));

            if (detailsType.equals("opportunity")) {
                // red triangle — has concrete time savings
                item.put("type", "opportunity");
                item.put("savingsMs", details.path("overallSavingsMs").asDouble(0));
                item.put("savingsBytes", details.path("overallSavingsBytes").asDouble(0));
            } else {
                // orange square — diagnostic, no time savings estimate
                item.put("type", "diagnostic");
                item.put("savingsMs", 0);
                item.put("savingsBytes", 0);
            }

            results.add(item);
        });

        // sort: opportunities first, then diagnostics, each group by score ascending
        results.sort((a, b) -> {
            String typeA = (String) a.get("type");
            String typeB = (String) b.get("type");
            if (!typeA.equals(typeB)) {
                return typeA.equals("opportunity") ? -1 : 1;
            }
            return Double.compare((double) a.get("score"), (double) b.get("score"));
        });

        return results;
    }
}
