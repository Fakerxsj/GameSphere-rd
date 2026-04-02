package com.xsj.interceptor;

import com.xsj.exception.AuthenticationException;
import com.xsj.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(jwtUtil.getTokenHeader());

        if (authorization != null && authorization.startsWith(jwtUtil.getTokenPrefix())) {
            String token = authorization.substring(jwtUtil.getTokenPrefix().length()).trim();

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
                request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
            } else {
                throw new AuthenticationException("Token 无效或已过期");
            }
        } else {
            throw new AuthenticationException("未提供认证信息");
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.contains("/auth/") ||
                path.contains("/api/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/webjars/");
    }
}