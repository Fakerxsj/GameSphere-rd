package com.xsj.crawler;

import com.xsj.entity.Game;
import com.xsj.util.HttpClientUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TapTapCrawler extends AbstractCrawler {

    private static final String BASE_URL = "https://www.taptap.cn";

    @PostConstruct
    public void init() {
        super.setHttpClientUtil(new HttpClientUtil());
    }

    @Override
    public List<Game> crawl(String url) throws IOException {
        log.info("开始爬取 TapTap: {}", url);

        String html = fetchPage(url);
        Document document = parseDocument(html);

        List<Game> games = new ArrayList<>();

        Elements gameElements = document.select(".game-list-item, .game-card, [data-game-id]");

        for (Element gameElement : gameElements) {
            try {
                Game game = parseGameElement(gameElement);
                if (isValidGame(game)) {
                    games.add(game);
                    log.debug("解析到游戏：{}", game.getName());
                }
            } catch (Exception e) {
                log.error("解析游戏元素失败", e);
            }
        }

        if (games.isEmpty()) {
            log.warn("未解析到游戏数据，尝试详情页爬取");
            Game game = crawlGameDetail(url);
            if (game != null) {
                games.add(game);
            }
        }

        log.info("成功爬取 {} 个游戏", games.size());
        return games;
    }

    @Override
    public List<Game> crawlGameList(String listUrl) throws IOException {
        log.info("爬取游戏列表：{}", listUrl);

        List<Game> allGames = new ArrayList<>();
        int page = 1;
        boolean hasMore = true;

        while (hasMore && page <= 10) {
            String paginatedUrl = listUrl + "?page=" + page;
            log.info("爬取第 {} 页：{}", page, paginatedUrl);

            try {
                String html = fetchPage(paginatedUrl);
                Document document = parseDocument(html);

                List<Game> games = parseGameList(document);

                if (games.isEmpty()) {
                    hasMore = false;
                    break;
                }

                allGames.addAll(games);
                log.info("第 {} 页爬取成功，共 {} 个游戏", page, games.size());

                page++;

                if (delay > 0) {
                    Thread.sleep(delay);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                log.error("爬取第 {} 页失败", page, e);
                break;
            }
        }

        log.info("游戏列表爬取完成，总共 {} 个游戏", allGames.size());
        return allGames;
    }

    @Override
    public Game crawlGameDetail(String detailUrl) throws IOException {
        log.info("爬取游戏详情：{}", detailUrl);

        String html = fetchPage(detailUrl);

        if (html == null || html.isEmpty()) {
            log.error("获取的页面内容为空");
            return null;
        }

        log.debug("页面内容长度：{}", html.length());

        Document document = parseDocument(html);

        Game game = new Game();

        try {
            Element rootElement = document.body();

            // 1. 爬取游戏名字（中文）
            String gameName = extractText(rootElement, "h1.game-title, h1");
            game.setName(gameName);

            // 2. 自动转换为英文名字（拼音）
            String englishName = convertToEnglishName(gameName);
            game.setEnglishName(englishName);

            // 3. 爬取开发商（右上角头像旁边的名字，如"心动"）
            String developer = null;
            Elements publisherInfo = rootElement.select("[class*='publisher-info'], [class*='dev-info']");
            if (!publisherInfo.isEmpty()) {
                developer = extractText(publisherInfo.first(), "[class*='name'], a");
            }

            if (developer == null) {
                Elements rightPanel = rootElement.select("[class*='right-panel'], [class*='sidebar']");
                if (!rightPanel.isEmpty()) {
                    Elements userElements = rightPanel.select("[class*='user-name'], [class*='nickname'], [class*='title']");
                    if (!userElements.isEmpty()) {
                        developer = userElements.first().text().trim();
                    }
                }
            }

            if (developer == null) {
                Elements linkElements = rootElement.select("[class*='publisher'] a, [class*='developer'] a");
                if (!linkElements.isEmpty()) {
                    developer = linkElements.first().text().trim();
                }
            }

            game.setDeveloper(developer);
            game.setPublisher(developer);

            // 4. 爬取评论总数（"详情"标签旁边的评价数）
            String commentCountText = null;
            Elements navItems = rootElement.select("[class*='nav'] span, [class*='tab'] span, .nav-item, .tab-item");
            for (Element navItem : navItems) {
                String text = navItem.text().trim();
                if (text.contains("评价") || text.contains("评论")) {
                    commentCountText = text.replaceAll("评价", "").replaceAll("评论", "").trim();
                    break;
                }
            }

            if (commentCountText == null) {
                Elements countElements = rootElement.select("[class*='count'], [class*='num'], [class*='total']");
                for (Element element : countElements) {
                    String text = element.text().trim();
                    if (text.contains("万") || (text.matches("\\d+") && text.length() > 3)) {
                        commentCountText = text;
                        break;
                    }
                }
            }

            if (commentCountText != null) {
                try {
                    String countStr = commentCountText.replaceAll("[^\\d.万]", "");
                    if (countStr.contains("万")) {
                        double wanCount = Double.parseDouble(countStr.replace("万", ""));
                        game.setCommentCount((int) (wanCount * 10000));
                    } else if (!countStr.isEmpty()) {
                        game.setCommentCount(Integer.parseInt(countStr));
                    }
                } catch (NumberFormatException e) {
                    log.warn("评论数解析失败：{}", commentCountText);
                }
            }

            // 5. 爬取封面图片（实机演示旁边图集中的第一张）
            String coverUrl = null;
            Elements galleryImages = rootElement.select("[class*='gallery'] img, [class*='screenshot'] img, [class*='album'] img");
            if (!galleryImages.isEmpty()) {
                coverUrl = galleryImages.first().attr("src");
            }

            if (coverUrl == null || coverUrl.isEmpty()) {
                Elements allImages = rootElement.select("img");
                for (Element img : allImages) {
                    String src = img.attr("src");
                    if (isValidGameImage(src) && !src.contains("logo") && !src.contains("avatar")) {
                        coverUrl = src;
                        break;
                    }
                }
            }
            game.setCoverImage(filterImageUrl(coverUrl));

            // 6. 爬取视频链接（宣传片，实机演示左边的按钮对应的视频）
            String videoUrl = extractAttr(rootElement, "video source, video", "src");
            if (videoUrl == null) {
                videoUrl = extractAttr(rootElement, "[class*='video'] iframe", "src");
            }
            if (videoUrl == null) {
                Elements videoButtons = rootElement.select("[class*='video-btn'] a, [class*='promo-btn'] a, [class*='trailer'] a");
                for (Element btn : videoButtons) {
                    String href = btn.attr("href");
                    if (href != null && (href.contains("video") || href.contains("youtube") || href.contains("bilibili") || href.contains("taptap"))) {
                        videoUrl = href;
                        break;
                    }
                }
            }
            if (videoUrl == null) {
                Elements videoElements = rootElement.select("[class*='video-player'], [class*='promo-player']");
                if (!videoElements.isEmpty()) {
                    videoUrl = extractAttr(videoElements.first(), "data-url", null);
                    if (videoUrl == null) {
                        videoUrl = extractAttr(videoElements.first(), "data-video", null);
                    }
                }
            }
            game.setVideoUrl(filterImageUrl(videoUrl));
            game.setTrailerUrl(filterImageUrl(videoUrl));

            // 7. 爬取下载量（"热门下载榜"旁边的"下载"数据）
            String downloadCountText = null;
            Elements statsElements = rootElement.select("[class*='stat'], [class*='info-item']");
            for (Element stat : statsElements) {
                String label = extractText(stat, "[class*='label'], span:first-child");
                String value = extractText(stat, "[class*='value'], span:last-child, .num");

                if (label != null && label.contains("下载")) {
                    downloadCountText = value;
                    break;
                }

                if (value != null && (value.contains("万") || value.matches("\\d+"))) {
                    Elements siblings = stat.nextElementSiblings();
                    for (Element sibling : siblings) {
                        String siblingText = sibling.text().trim();
                        if (siblingText.contains("下载")) {
                            downloadCountText = value;
                            break;
                        }
                    }
                }
            }

            if (downloadCountText == null) {
                Elements downloadElements = rootElement.select("[class*='download-count'], [class*='download-num']");
                if (!downloadElements.isEmpty()) {
                    downloadCountText = downloadElements.first().text().trim();
                }
            }

            if (downloadCountText != null) {
                try {
                    String countStr = downloadCountText.replaceAll("[^\\d.万]", "");
                    if (countStr.contains("万")) {
                        double wanCount = Double.parseDouble(countStr.replace("万", ""));
                        game.setDownloadCount((int) (wanCount * 10000));
                    } else if (!countStr.isEmpty()) {
                        game.setDownloadCount(Integer.parseInt(countStr));
                    }
                } catch (NumberFormatException e) {
                    log.warn("下载量解析失败：{}", downloadCountText);
                }
            }

            // 8. 爬取其他信息
            // 评分
            String ratingScoreText = extractText(rootElement, "[class*='score'], [class*='rating']");
            if (ratingScoreText != null) {
                try {
                    game.setRatingScore(new BigDecimal(ratingScoreText.trim()));
                } catch (NumberFormatException e) {
                    log.warn("评分解析失败：{}", ratingScoreText);
                }
            }

            // 评分人数
            String ratingCountText = extractText(rootElement, "[class*='rating-count'], [class*='score-count']");
            if (ratingCountText != null) {
                try {
                    String countStr = ratingCountText.replaceAll("[^\\d.万]", "");
                    if (countStr.contains("万")) {
                        double wanCount = Double.parseDouble(countStr.replace("万", ""));
                        game.setRatingCount((int) (wanCount * 10000));
                    } else {
                        game.setRatingCount(Integer.parseInt(countStr));
                    }
                } catch (NumberFormatException e) {
                    log.warn("评分人数解析失败：{}", ratingCountText);
                }
            }

            // 关注人数
            String followCountText = extractText(rootElement, "[class*='follow'], [class*='want']");
            if (followCountText != null) {
                try {
                    String countStr = followCountText.replaceAll("[^\\d.万]", "");
                    if (countStr.contains("万")) {
                        double wanCount = Double.parseDouble(countStr.replace("万", ""));
                        game.setFollowCount((int) (wanCount * 10000));
                    } else {
                        game.setFollowCount(Integer.parseInt(countStr));
                    }
                } catch (NumberFormatException e) {
                    log.warn("关注人数解析失败：{}", followCountText);
                }
            }

            // 游戏描述
            String description = extractText(rootElement, ".game-desc, .description, [class*='intro'], [class*='summary']");
            game.setDescription(description);

            // 游戏类型/标签
            List<String> tags = extractTexts(rootElement, "[class*='tag'] span, [class*='genre'] span, .tag-item");
            if (!tags.isEmpty()) {
                game.setGameType(joinStrings(tags));
            }

            // 平台
            String platformText = extractText(rootElement, "[class*='platform'], [class*='device']");
            game.setPlatform(platformText);

            // 上线日期
            String releaseDateText = extractText(rootElement, "[class*='release-date'], [class*='launch-date']");
            game.setReleaseDate(parseDate(releaseDateText));

            // 官方网站
            String officialWebsite = extractAttr(rootElement, "[class*='official'] a, [class*='website'] a", "href");
            game.setOfficialWebsite(officialWebsite);

            // 基础信息
            game.setSourceUrl(detailUrl);
            game.setSourceSite("TapTap");
            game.setStatus(1);
            game.setCreateTime(new Date());
            game.setUpdateTime(new Date());

            log.info("解析到的游戏名称：{}", game.getName());
            log.info("解析到的英文名称：{}", game.getEnglishName());
            log.info("解析到的开发商：{}", game.getDeveloper());
            log.info("解析到的封面图：{}", game.getCoverImage());
            log.info("解析到的视频链接：{}", game.getVideoUrl());
            log.info("解析到的评论数：{}", game.getCommentCount());
            log.info("解析到的下载量：{}", game.getDownloadCount());

            if (isValidGame(game)) {
                log.info("游戏详情爬取成功：{}", game.getName());
                return game;
            } else {
                log.warn("游戏详情解析失败，数据不完整 - 名称：{}, 封面：{}", game.getName(), game.getCoverImage());
                return null;
            }

        } catch (Exception e) {
            log.error("解析游戏详情失败", e);
            return null;
        }
    }
// ... existing code ...



    /**
     * 将中文游戏名转换为英文名称（拼音）
     */
    private String convertToEnglishName(String chineseName) {
        if (chineseName == null || chineseName.isEmpty()) {
            return null;
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (char c : chineseName.toCharArray()) {
                if (Character.isLetter(c) || Character.isDigit(c) || c == ' ') {
                    sb.append(c);
                } else {
                    sb.append(c);
                }
            }

            String result = sb.toString().trim();
            result = result.replaceAll("\\s+", "-");

            if (result.isEmpty()) {
                return chineseName;
            }

            return result;
        } catch (Exception e) {
            log.warn("英文名称转换失败：{}", chineseName);
            return chineseName;
        }
    }

    private String filterImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        if (url.contains("log.aliyuncs.com") ||
                url.contains("track") ||
                url.contains("analytics") ||
                url.contains("monitor") ||
                url.contains("gif")) {
            log.debug("过滤追踪链接：{}", url);
            return null;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("//")) {
            url = normalizeImageUrl(BASE_URL, url);
        }

        if (url.startsWith("//")) {
            url = "https:" + url;
        }

        if (url.length() > 1000) {
            log.debug("URL 过长，可能无效");
            return null;
        }

        return url;
    }

    private boolean isValidGameImage(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        return url.contains("cdn") ||
                url.contains("image") ||
                url.contains("img") ||
                url.contains("cover") ||
                url.contains("icon") ||
                url.contains("screenshot") ||
                url.contains("gallery") ||
                url.endsWith(".jpg") ||
                url.endsWith(".jpeg") ||
                url.endsWith(".png") ||
                url.endsWith(".webp");
    }

    private Game parseGameElement(Element element) {
        Game game = new Game();

        try {
            game.setName(extractText(element, ".game-title, .title, a.name"));
            game.setEnglishName(convertToEnglishName(game.getName()));

            String linkUrl = extractAttr(element, "a[href]", "href");
            if (linkUrl != null && !linkUrl.isEmpty()) {
                String gameId = extractGameIdFromUrl(linkUrl);
                if (gameId != null) {
                    game.setId(Long.parseLong(gameId));
                }
            }

            String coverUrl = extractAttr(element, "img.cover, .cover img, img", "src");
            game.setCoverImage(normalizeImageUrl(BASE_URL, coverUrl));

            game.setGameType(extractText(element, ".game-type, .type"));

            String ratingText = extractText(element, ".rating, .score");
            if (ratingText != null) {
                try {
                    game.setRatingScore(new BigDecimal(ratingText.trim()));
                } catch (NumberFormatException e) {
                    log.debug("列表页评分解析失败：{}", ratingText);
                }
            }

            String priceText = extractText(element, ".price");
            game.setPrice(parsePrice(priceText));

            game.setSourceUrl(BASE_URL + extractAttr(element, "a[href]", "href"));
            game.setSourceSite("TapTap");
            game.setStatus(1);
            game.setCreateTime(new Date());
            game.setUpdateTime(new Date());

        } catch (Exception e) {
            log.error("解析游戏卡片元素失败", e);
        }

        return game;
    }

    private List<Game> parseGameList(Document document) {
        List<Game> games = new ArrayList<>();

        Elements gameElements = document.select(".game-list-item, .game-card, [data-game-id]");

        for (Element element : gameElements) {
            try {
                Game game = parseGameElement(element);
                if (isValidGame(game)) {
                    games.add(game);
                }
            } catch (Exception e) {
                log.error("解析游戏卡片失败", e);
            }
        }

        return games;
    }

    private String extractGameIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        String[] parts = url.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty() && parts[i].matches("\\d+")) {
                return parts[i];
            }
        }

        return null;
    }

    public List<Game> crawlPopularGames() throws IOException {
        String popularUrl = BASE_URL + "/discover/popular";
        log.info("爬取热门游戏：{}", popularUrl);
        return crawlGameList(popularUrl);
    }

    public List<Game> crawlNewGames() throws IOException {
        String newUrl = BASE_URL + "/discover/new";
        log.info("爬取新游戏：{}", newUrl);
        return crawlGameList(newUrl);
    }

    public List<Game> crawlGamesByCategory(String category) throws IOException {
        String categoryUrl = BASE_URL + "/category/" + category;
        log.info("爬取分类 {} 的游戏：{}", category, categoryUrl);
        return crawlGameList(categoryUrl);
    }
}
