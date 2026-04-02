package com.xsj.interceptor;

import com.xsj.entity.SystemLog;
import com.xsj.service.SystemLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final SystemLogService systemLogService;

    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTimeHolder.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Long userId = (Long) request.getAttribute("userId");

        long executeTime = System.currentTimeMillis() - startTimeHolder.get();
        startTimeHolder.remove();

        SystemLog systemLog = new SystemLog();
        systemLog.setUserId(userId);
        systemLog.setModule(request.getRequestURI());
        systemLog.setOperation(request.getMethod());
        systemLog.setMethod(handler.toString());
        systemLog.setIpAddress(getIpAddress(request));
        systemLog.setUserAgent(request.getHeader("User-Agent"));
        systemLog.setExecuteTime(executeTime);
        systemLog.setStatus(response.getStatus() < 400 ? 1 : 0);

        CompletableFuture.runAsync(() -> {
            try {
                systemLogService.save(systemLog);
            } catch (Exception e) {
                log.error("记录系统日志失败", e);
            }
        });
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
