package com.xsj.crawler.igdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IgdbApiClient {

    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.client-secret}")
    private String clientSecret;

    @Value("${igdb.token-url}")
    private String tokenUrl;

    @Value("${igdb.base-url}")
    private String baseUrl;

    private OkHttpClient tokenClient;  // 用于获取 Token（走代理）
    private OkHttpClient apiClient;    // 用于调用 IGDB API（直连）
    private final ObjectMapper objectMapper;
    private String accessToken;
    private long tokenExpireTime;

    public IgdbApiClient() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        try {
            // 创建信任所有证书的 TrustManager
            javax.net.ssl.X509TrustManager trustAllCerts = new javax.net.ssl.X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            };

            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{trustAllCerts}, new java.security.SecureRandom());

            // 1. Token 客户端：使用代理（访问 id.twitch.tv）
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS,
                    new java.net.InetSocketAddress("127.0.0.1", 9674));
            this.tokenClient = new OkHttpClient.Builder()
                    .proxy(proxy)
                    .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            log.info("Token 客户端已配置代理：127.0.0.1:9674");

            // 2. API 客户端：直连 IGDB（不走代理）
            this.apiClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            log.info("IGDB API 客户端已配置为直连模式");

        } catch (Exception e) {
            log.error("初始化失败", e);
            this.tokenClient = new OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            this.apiClient = this.tokenClient;
        }
    }

    public String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        try {
            RequestBody body = RequestBody.create(
                    "client_id=" + clientId +
                            "&client_secret=" + clientSecret +
                            "&grant_type=client_credentials",
                    MediaType.parse("application/x-www-form-urlencoded")
            );

            Request request = new Request.Builder()
                    .url(tokenUrl)
                    .post(body)
                    .build();

            // 使用带代理的客户端获取 Token
            try (Response response = tokenClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("HTTP 请求失败：" + response.code());
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                accessToken = jsonNode.get("access_token").asText();
                long expiresIn = jsonNode.get("expires_in").asLong();
                tokenExpireTime = System.currentTimeMillis() + (expiresIn - 60) * 1000;

                log.info("IGDB Access Token 刷新成功");
            }
        } catch (Exception e) {
            log.error("刷新 IGDB Access Token 失败", e);
            throw new RuntimeException("获取 IGDB Token 失败: " + e.getMessage(), e);
        }
    }

    public JsonNode searchGames(String query, int limit) {
        try {
            // 修改：alternative_name -> alternative_names, developers/publishers -> involved_companies.company.name
            String apicalypseQuery = String.format(
                    "search \"%s\"; " +
                            "fields name,alternative_names,cover.url,artworks.url,screenshots.url,genres.name," +
                            "involved_companies.company.name,involved_companies.developer,involved_companies.publisher," +
                            "first_release_date,platforms.name,rating,rating_count," +
                            "summary,websites.url,videos.video_id,storyline; " +
                            "sort rating desc; limit %d;",
                    query, limit
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/games")
                    .post(RequestBody.create(apicalypseQuery, MediaType.parse("text/plain")))
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .build();

            try (Response response = apiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Empty response";
                    log.error("搜索游戏失败：HTTP {}, 详情: {}", response.code(), errorBody);
                    return objectMapper.createArrayNode();
                }
                return objectMapper.readTree(response.body().string());
            }
        } catch (Exception e) {
            log.error("搜索游戏失败：{}", query, e);
            return objectMapper.createArrayNode();
        }
    }
    public JsonNode getPopularGames(int limit) {
        return getPopularGames(limit, 0);
    }
    public JsonNode getPopularGames(int limit, int offset) {
        try {
            // 移除 aliases，修正 involved_companies 查询
            String apicalypseQuery = String.format(
                    "fields name,alternative_names,cover.url,artworks.url,screenshots.url,genres.name," +
                            "involved_companies.company.name,involved_companies.developer,involved_companies.publisher," +
                            "first_release_date,platforms.name,rating,rating_count," +
                            "summary,websites.url,videos.video_id,storyline; " +
                            "sort rating desc; limit %d; offset %d;",
                    limit, offset
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/games")
                    .post(RequestBody.create(apicalypseQuery, MediaType.parse("text/plain")))
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .build();

            try (Response response = apiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Empty response";
                    log.error("获取热门游戏失败：HTTP {}, 详情: {}", response.code(), errorBody);
                    return objectMapper.createArrayNode();
                }
                return objectMapper.readTree(response.body().string());
            }
        } catch (Exception e) {
            log.error("获取热门游戏失败", e);
            return objectMapper.createArrayNode();
        }
    }

    public JsonNode getGamesByGenre(java.util.List<String> genres, int limit, java.util.List<Long> excludeIds) {
        try {
            String whereClause = "";
            if (!excludeIds.isEmpty()) {
                String excludeStr = excludeIds.stream()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(","));
                whereClause = String.format("where id != (%s) & genres = (%s); ", excludeStr, String.join(",", genres));
            } else {
                whereClause = String.format("where genres = (%s); ", String.join(",", genres));
            }

            String apicalypseQuery = String.format(
                    "%sfields name,cover.url,summary,genres.name,platforms.name," +
                            "first_release_date,rating,rating_count,aggregated_rating,aggregated_rating_count," +
                            "involved_companies.company.name,videos.video_id,cover.image_id; " +
                            "sort aggregated_rating desc; limit %d;",
                    whereClause, limit
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/games")
                    .post(RequestBody.create(apicalypseQuery, MediaType.parse("text/plain")))
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .build();

            try (Response response = apiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("按类型获取游戏失败：HTTP {}", response.code());
                    return objectMapper.createArrayNode();
                }
                return objectMapper.readTree(response.body().string());
            }
        } catch (Exception e) {
            log.error("按类型获取游戏失败", e);
            return objectMapper.createArrayNode();
        }
    }

    public JsonNode getGameById(long igdbId) {
        try {
            String apicalypseQuery = String.format(
                    "where id = %d; " +
                            "fields name,alternative_names,cover.url,artworks.url,screenshots.url,genres.name," +
                            "involved_companies.company.name,involved_companies.developer,involved_companies.publisher," +
                            "first_release_date,platforms.name,rating,rating_count," +
                            "summary,websites.url,videos.video_id,storyline; " +
                            "sort rating desc; limit %d;",
                    igdbId
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/games")
                    .post(RequestBody.create(apicalypseQuery, MediaType.parse("text/plain")))
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .build();

            try (Response response = apiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("获取游戏详情失败：HTTP {}", response.code());
                    return null;
                }
                JsonNode result = objectMapper.readTree(response.body().string());
                return result.isArray() && result.size() > 0 ? result.get(0) : null;
            }
        } catch (Exception e) {
            log.error("获取游戏详情失败：{}", igdbId, e);
            return null;
        }
    }
}
