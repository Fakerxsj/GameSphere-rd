package com.xsj.exception;

import lombok.Getter;

@Getter
public class CrawlerException extends RuntimeException {

    private final Integer code;
    private final String url;

    public CrawlerException(String message, String url) {
        super(message);
        this.code = 500;
        this.url = url;
    }

    public CrawlerException(Integer code, String message, String url) {
        super(message);
        this.code = code;
        this.url = url;
    }
}
