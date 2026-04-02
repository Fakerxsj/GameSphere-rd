package com.xsj.interceptor;

import com.xsj.entity.UserBehavior;
import com.xsj.service.UserBehaviorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBehaviorInterceptor implements HandlerInterceptor {

    private final UserBehaviorService userBehaviorService;

    private static final Pattern GAME_ID_PATTERN = Pattern.compile("/api/game/(?:detail|click)/(\\d+)");

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            return;
        }

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return;
        }

        String uri = request.getRequestURI();
        Matcher matcher = GAME_ID_PATTERN.matcher(uri);

        if (matcher.find()) {
            Long gameId = Long.parseLong(matcher.group(1));

            CompletableFuture.runAsync(() -> {
                try {
                    UserBehavior behavior = new UserBehavior();
                    behavior.setUserId(userId);
                    behavior.setGameId(gameId);
                    behavior.setBehaviorType("view");
                    behavior.setBehaviorTime(new Date());
                    behavior.setDevice(request.getHeader("User-Agent"));
                    behavior.setIpAddress(getIpAddress(request));

                    userBehaviorService.save(behavior);
                } catch (Exception e) {
                    log.error("记录用户行为失败", e);
                }
            });
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
