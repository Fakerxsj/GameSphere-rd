package com.xsj.crawler;

import com.xsj.entity.Game;
import com.xsj.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCrawler {

    @Value("${crawler.user-agent}")
    protected String userAgent;

    @Value("${crawler.timeout}")
    protected Integer timeout;

    @Value("${crawler.delay}")
    protected Integer delay;

    @Value("${crawler.max-retry}")
    protected Integer maxRetry;

    protected HttpClientUtil httpClientUtil;

    public void setHttpClientUtil(HttpClientUtil httpClientUtil) {
        this.httpClientUtil = httpClientUtil;
    }

    protected String fetchPage(String url) throws IOException {
        int retryCount = 0;
        while (retryCount < maxRetry) {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", userAgent);
                headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
                headers.put("Referer", "https://www.taptap.cn/");
                headers.put("Connection", "keep-alive");

                log.info("开始请求 URL: {}, 超时时间：{}ms", url, timeout);
                String html = httpClientUtil.get(url, headers);

                log.info("请求成功，HTML 长度：{}", html != null ? html.length() : 0);

                if (delay > 0) {
                    Thread.sleep(delay);
                }

                return html;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("请求被中断", e);
            } catch (IOException e) {
                retryCount++;
                log.warn("请求失败，第 {} 次重试，URL: {}, 错误：{}", retryCount, url, e.getMessage());
                if (retryCount >= maxRetry) {
                    throw e;
                }
                try {
                    Thread.sleep(2000 * retryCount);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IOException("达到最大重试次数");
    }

    protected Document parseDocument(String html) {
        return org.jsoup.Jsoup.parse(html);
    }

    protected String extractText(Element element, String cssQuery) {
        if (element == null) return null;
        Element target = element.selectFirst(cssQuery);
        return target != null ? target.text().trim() : null;
    }

    protected String extractAttr(Element element, String cssQuery, String attr) {
        if (element == null) return null;
        Element target = element.selectFirst(cssQuery);
        return target != null ? target.attr(attr) : null;
    }

    protected List<String> extractTexts(Element element, String cssQuery) {
        if (element == null) return Collections.emptyList();
        Elements targets = element.select(cssQuery);
        return targets.stream()
                .map(Element::text)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    protected BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return BigDecimal.ZERO;
        }

        priceText = priceText.replaceAll("[^\\d.]", "");

        try {
            return new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    protected Date parseDate(String dateText) {
        if (dateText == null || dateText.isEmpty()) {
            return null;
        }

        SimpleDateFormat[] formats = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("yyyy/MM/dd"),
                new SimpleDateFormat("yyyy 年 MM 月 dd 日"),
                new SimpleDateFormat("MM-dd-yyyy"),
                new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH),
                new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        };

        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(dateText);
            } catch (ParseException e) {
                continue;
            }
        }

        log.warn("日期解析失败：{}", dateText);
        return null;
    }

    protected String normalizeImageUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return null;
        }

        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }

        if (relativeUrl.startsWith("//")) {
            return "https:" + relativeUrl;
        }

        if (relativeUrl.startsWith("/")) {
            return baseUrl + relativeUrl;
        }

        return baseUrl + "/" + relativeUrl;
    }

    protected String joinStrings(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }

    protected boolean isValidGame(Game game) {
        return game != null &&
                game.getName() != null &&
                !game.getName().isEmpty() &&
                game.getCoverImage() != null;
    }

    public abstract List<Game> crawl(String url) throws IOException;

    public abstract List<Game> crawlGameList(String listUrl) throws IOException;

    public abstract Game crawlGameDetail(String detailUrl) throws IOException;
}
