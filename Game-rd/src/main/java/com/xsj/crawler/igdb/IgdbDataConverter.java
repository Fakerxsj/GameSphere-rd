package com.xsj.crawler.igdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.xsj.entity.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class IgdbDataConverter {

    public Game convertToGame(JsonNode gameNode) {
        if (gameNode == null || gameNode.isNull()) {
            return null;
        }

        Game game = new Game();

        try {
            if (gameNode.has("id")) {
                game.setIgdbId(gameNode.get("id").asLong());
            }

            game.setName(gameNode.has("name") ? gameNode.get("name").asText() : "Unknown");

            // 处理英文名称 (alternative_names 是数组)
            if (gameNode.has("alternative_names") && gameNode.get("alternative_names").isArray() && gameNode.get("alternative_names").size() > 0) {
                game.setEnglishName(gameNode.get("alternative_names").get(0).asText());
            } else {
                game.setEnglishName(game.getName());
            }

            // 处理封面图
            if (gameNode.has("cover") && !gameNode.get("cover").isNull()) {
                JsonNode cover = gameNode.get("cover");
                if (cover.has("url")) {
                    String coverUrl = cover.get("url").asText();
                    coverUrl = coverUrl.replace("t_thumb", "t_cover_big");
                    coverUrl = coverUrl.startsWith("http") ? coverUrl : "https:" + coverUrl;
                    game.setCoverImage(coverUrl);
                }
            }

            // 处理 Banner 图 (Artworks)
            if (gameNode.has("artworks") && gameNode.get("artworks").isArray() && gameNode.get("artworks").size() > 0) {
                JsonNode artwork = gameNode.get("artworks").get(0);
                if (artwork.has("url")) {
                    String bannerUrl = artwork.get("url").asText();
                    bannerUrl = bannerUrl.replace("t_thumb", "t_1080p");
                    bannerUrl = bannerUrl.startsWith("http") ? bannerUrl : "https:" + bannerUrl;
                    game.setBannerImage(bannerUrl);
                }
            }

            // 处理截图
            if (gameNode.has("screenshots") && gameNode.get("screenshots").isArray()) {
                List<String> screenshots = new ArrayList<>();
                JsonNode screenshotsNode = gameNode.get("screenshots");
                int count = Math.min(screenshotsNode.size(), 5); // 最多取 5 张
                for (int i = 0; i < count; i++) {
                    String url = screenshotsNode.get(i).get("url").asText();
                    url = url.replace("t_thumb", "t_720p");
                    url = url.startsWith("http") ? url : "https:" + url;
                    screenshots.add(url);
                }
                game.setScreenshots(String.join(",", screenshots));
            }

            // 评分处理 (IGDB 评分通常是 0-100，转为 0-10)
            if (gameNode.has("rating") && !gameNode.get("rating").isNull()) {
                game.setRatingScore(new java.math.BigDecimal(gameNode.get("rating").asDouble() / 10));
            }
            if (gameNode.has("rating_count") && !gameNode.get("rating_count").isNull()) {
                game.setRatingCount(gameNode.get("rating_count").asInt());
            }

            // 开发商和发行商
            List<String> devs = new ArrayList<>();
            List<String> pubs = new ArrayList<>();
            if (gameNode.has("involved_companies") && gameNode.get("involved_companies").isArray()) {
                for (JsonNode rel : gameNode.get("involved_companies")) {
                    if (rel.has("company") && !rel.get("company").isNull()) {
                        String companyName = rel.get("company").has("name") ? rel.get("company").get("name").asText() : null;
                        if (companyName != null) {
                            if (rel.has("developer") && rel.get("developer").asBoolean()) devs.add(companyName);
                            if (rel.has("publisher") && rel.get("publisher").asBoolean()) pubs.add(companyName);
                        }
                    }
                }
            }
            String devStr = String.join(", ", devs);
            String pubStr = String.join(", ", pubs);

            game.setDeveloper(devStr.length() > 250 ? devStr.substring(0, 250) : devStr);
            game.setPublisher(pubStr.length() > 250 ? pubStr.substring(0, 250) : pubStr);


            // 处理英文名称 (alternative_names 是数组)
            if (gameNode.has("alternative_names") && gameNode.get("alternative_names").isArray() && gameNode.get("alternative_names").size() > 0) {
                game.setEnglishName(gameNode.get("alternative_names").get(0).asText());
            } else {
                game.setEnglishName(game.getName());
            }

            // 平台
            if (gameNode.has("platforms") && gameNode.get("platforms").isArray()) {
                List<String> platforms = new ArrayList<>();
                for (JsonNode platform : gameNode.get("platforms")) {
                    if (platform.has("name")) platforms.add(platform.get("name").asText());
                }
                game.setPlatform(String.join(", ", platforms));
            }

            // 游戏类型
            if (gameNode.has("genres") && gameNode.get("genres").isArray()) {
                List<String> genres = new ArrayList<>();
                for (JsonNode genre : gameNode.get("genres")) {
                    if (genre.has("name")) genres.add(genre.get("name").asText());
                }
                String joinedGenres = String.join(", ", genres);
                // 限制最大长度为 250 字符，防止数据库溢出
                game.setGameType(joinedGenres.length() > 250 ? joinedGenres.substring(0, 250) + "..." : joinedGenres);
            }

            // 发售日期
            if (gameNode.has("first_release_date") && !gameNode.get("first_release_date").isNull()) {
                long timestamp = gameNode.get("first_release_date").asLong();
                game.setReleaseDate(new Date(timestamp * 1000));
            }

            // 描述 (Summary + Storyline)
            StringBuilder desc = new StringBuilder();
            if (gameNode.has("summary") && !gameNode.get("summary").isNull()) {
                desc.append(gameNode.get("summary").asText());
            }
            if (gameNode.has("storyline") && !gameNode.get("storyline").isNull()) {
                if (desc.length() > 0) desc.append("\n\n");
                desc.append(gameNode.get("storyline").asText());
            }
            game.setDescription(desc.toString());


            // 官网
            if (gameNode.has("websites") && gameNode.get("websites").isArray() && gameNode.get("websites").size() > 0) {
                game.setOfficialWebsite(gameNode.get("websites").get(0).get("url").asText());
            }

            // 视频/预告片
            if (gameNode.has("videos") && gameNode.get("videos").isArray() && gameNode.get("videos").size() > 0) {
                String videoId = gameNode.get("videos").get(0).get("video_id").asText();
                game.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);
                game.setTrailerUrl("https://www.youtube.com/embed/" + videoId);
            }

            game.setSourceSite("IGDB");
            game.setStatus(1);
            game.setCreateTime(new Date());
            game.setUpdateTime(new Date());

            return game;
        } catch (Exception e) {
            log.error("转换游戏数据失败", e);
            return null;
        }
    }

    public List<String> extractTags(JsonNode gameNode) {
        List<String> tags = new ArrayList<>();

        if (gameNode.has("genres") && gameNode.get("genres").isArray()) {
            for (JsonNode genre : gameNode.get("genres")) {
                if (genre.has("name")) {
                    tags.add(genre.get("name").asText());
                }
            }
        }

        if (gameNode.has("platforms") && gameNode.get("platforms").isArray()) {
            for (JsonNode platform : gameNode.get("platforms")) {
                if (platform.has("name")) {
                    tags.add(platform.get("name").asText());
                }
            }
        }

        return tags;
    }
}
