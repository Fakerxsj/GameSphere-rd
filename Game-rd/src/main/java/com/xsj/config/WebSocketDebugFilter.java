package com.xsj.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class WebSocketDebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        String upgrade = httpRequest.getHeader("Upgrade");

        if (uri.contains("/ws/")) {
            log.info("🔍 检测到 WebSocket 相关请求: URI={}, Method={}, Upgrade={}, Headers={}",
                    uri, httpRequest.getMethod(), upgrade, httpRequest.getHeaderNames());
        }

        chain.doFilter(request, response);
    }
}
