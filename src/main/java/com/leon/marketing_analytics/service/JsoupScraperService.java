package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.SeoCheckResult;
import com.leon.marketing_analytics.entity.CheckStatus;
import com.leon.marketing_analytics.exception.SeoAnalysisException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
public class JsoupScraperService {

    private List<SeoCheckResult> passedCheck(List<SeoCheckResult> result, String checkName) {
        if (result.isEmpty()) {
            String successMessage = " checked. All good!";
            result.add(new SeoCheckResult(checkName, CheckStatus.PASS,
                    checkName + successMessage));
        }

        return result;
    }

    private List<SeoCheckResult> titleCheck(String title, String keyword) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "Title";
        if (title.isBlank()) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    "The title is missing"));
        } else {
            if (title.length() > 60) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        "The title is too long. Suggest length: 50-60 characters"));
            }
            if (!keyword.isBlank() && !title.toLowerCase().contains(keyword)) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        "The title should contains the keyword: " + keyword));
            }
        }

        return passedCheck(checks, checkName);
    }

    private List<SeoCheckResult> metaDescriptionCheck(Element metaDescription, String keyword) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "Meta description";
        if (metaDescription == null || metaDescription.attr("content").isBlank()) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    checkName + " missing"));
        } else {
            if (metaDescription.attr("content").length() > 155) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        checkName + " too long. Suggest length: 120-155 characters"));
            }
            if (!keyword.isBlank() && !metaDescription.attr("content").toLowerCase().contains(keyword)) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        checkName + " missing keyword " + keyword));
            }
        }

        return passedCheck(checks, checkName);
    }

    private List<SeoCheckResult> h1Check(Elements h1s, String keyword) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "H1";
        if (h1s.isEmpty()) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    "H1 missing"));
        } else if (h1s.size() > 1) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    "More than one H1 found"));
        } else if (!keyword.isBlank() && !h1s.first().text().toLowerCase().contains(keyword)) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                    "H1 should contains the keyword: " + keyword));
        }

        return passedCheck(checks, checkName);
    }

    private List<SeoCheckResult> headingsCheck(Elements headings) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "Headings hierarchy";

        List<String> tags = headings.stream().map(Element::tagName).toList();
        if (tags.size() < 3) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                    checkName + " improvement: add more headings"));
        }
        if (!tags.isEmpty() && !tags.getFirst().equals("h1")) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    "H1 should be on the highest hierarchy"));
        }
        for (int i = 1; i < tags.size(); i++) {
            if (tags.get(i).compareTo(tags.get(i - 1)) > 1) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        checkName + " skipped: " + tags.get(i) + " should be placed after one hierarchy above"));
            }
        }

        return passedCheck(checks, checkName);
    }

    private List<SeoCheckResult> imageCheck(Elements images) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "Images";

        images.forEach(img -> {
            if (!img.hasAttr("alt")) {
                checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                        img.attr("src") + " is missing alt text"));
            }
        });

        return passedCheck(checks, checkName);
    }

    private List<SeoCheckResult> linksCheck(String domain, Elements links) {
        List<SeoCheckResult> checks = new ArrayList<>();
        String checkName = "Internal links";
        String baseHost;
        try {
            URI base = new URI(domain);
            baseHost = base.getScheme() + "://" + base.getHost();
        } catch (Exception e) {
            return checks;
        }

        Long internalLinkCount = links.stream().map(link -> link.absUrl("href"))
                .filter(href -> !href.isBlank())
                .filter(href -> href.startsWith(baseHost)).count();

        if (internalLinkCount == 0) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.FAIL,
                    "This is a orphan page. Suggest have 3-8 contextual internal links."));
        } else if (internalLinkCount < 3) {
            checks.add(new SeoCheckResult(checkName, CheckStatus.WARN,
                    "Suggest have 3-8 contextual internal links."));
        }

        return passedCheck(checks, checkName);
    }


    public List<SeoCheckResult> scrapeUrl(String url, String keyword) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();
        } catch (IOException e) {
            throw new SeoAnalysisException("Failed to connect with the url");
        }

        keyword = (keyword != null) ? keyword.toLowerCase().trim() : "";
        String title = doc.title();
        Element metaDescription = doc.selectFirst("meta[name=description]");
        Elements h1s = doc.select("h1");
        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
        Elements images = doc.select("img");
        Elements links = doc.select("a[href]");

        return Stream.of(
                titleCheck(title, keyword),
                metaDescriptionCheck(metaDescription, keyword),
                h1Check(h1s, keyword),
                headingsCheck(headings),
                imageCheck(images),
                linksCheck(url, links)
        ).flatMap(Collection::stream).toList();
    }

}
