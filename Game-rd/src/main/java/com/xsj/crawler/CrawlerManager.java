package com.xsj.crawler;

import com.xsj.exception.CrawlerException;
import com.xsj.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerManager {

    private final TapTapCrawler tapTapCrawler;

    public List<Game> crawlFromTapTap(String url) throws CrawlerException {
        log.info("开始从 TapTap 爬取游戏数据，URL: {}", url);
        try {
            return tapTapCrawler.crawl(url);
        } catch (Exception e) {
            log.error("从 TapTap 爬取失败", e);
            throw new CrawlerException("TapTap 爬取失败：" + e.getMessage(), url);
        }
    }

    @Async
    public CompletableFuture<List<Game>> crawlFromTapTapAsync(String url) {
        return CompletableFuture.supplyAsync(() -> crawlFromTapTap(url));
    }

    public List<Game> crawlGameList(String listUrl) throws CrawlerException {
        log.info("开始爬取 TapTap 游戏列表：{}", listUrl);
        try {
            return tapTapCrawler.crawlGameList(listUrl);
        } catch (Exception e) {
            log.error("爬取游戏列表失败", e);
            throw new CrawlerException("游戏列表爬取失败：" + e.getMessage(), listUrl);
        }
    }

    public Game crawlGameDetail(String detailUrl) throws CrawlerException {
        log.info("开始爬取 TapTap 游戏详情：{}", detailUrl);
        try {
            return tapTapCrawler.crawlGameDetail(detailUrl);
        } catch (Exception e) {
            log.error("爬取游戏详情失败", e);
            throw new CrawlerException("游戏详情爬取失败：" + e.getMessage(), detailUrl);
        }
    }

    public List<String> extractTagsFromDetail(String detailUrl) throws CrawlerException {
        log.info("开始从详情页提取标签：{}", detailUrl);
        try {
            return tapTapCrawler.extractTagsFromDetail(detailUrl);
        } catch (Exception e) {
            log.error("提取标签失败", e);
            throw new CrawlerException("标签提取失败：" + e.getMessage(), detailUrl);
        }
    }
}
