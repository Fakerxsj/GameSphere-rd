package com.xsj.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HttpClientUtil {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final int DEFAULT_TIMEOUT = 30000;

    public String get(String url) throws IOException {
        return get(url, null, null);
    }

    public String get(String url, Map<String, String> headers) throws IOException {
        return get(url, headers, null);
    }

    public String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        Connection connection = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .timeout(DEFAULT_TIMEOUT)
                .userAgent(DEFAULT_USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true);

        if (headers != null) {
            connection.headers(headers);
        }

        if (params != null) {
            connection.data(params);
        }

        Connection.Response response = connection.execute();

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            log.error("HTTP GET 请求失败，URL: {}, 状态码：{}", url, response.statusCode());
            throw new IOException("HTTP 错误：" + response.statusCode());
        }
    }

    public String post(String url, Map<String, String> data) throws IOException {
        return post(url, data, null);
    }

    public String post(String url, Map<String, String> data, Map<String, String> headers) throws IOException {
        Connection connection = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .timeout(DEFAULT_TIMEOUT)
                .userAgent(DEFAULT_USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .data(data);

        if (headers != null) {
            connection.headers(headers);
        }

        Connection.Response response = connection.execute();

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            log.error("HTTP POST 请求失败，URL: {}, 状态码：{}", url, response.statusCode());
            throw new IOException("HTTP 错误：" + response.statusCode());
        }
    }

    public Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(DEFAULT_TIMEOUT)
                .userAgent(DEFAULT_USER_AGENT)
                .get();
    }
}
