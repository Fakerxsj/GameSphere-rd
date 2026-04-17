package com.xsj.config;

import com.xsj.interceptor.LogInterceptor;
import com.xsj.interceptor.UserBehaviorInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;
    private final UserBehaviorInterceptor userBehaviorInterceptor;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/api/ws/**");

        registry.addInterceptor(userBehaviorInterceptor)
                .addPathPatterns("/game/detail/**", "/game/click/**");
    }



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始配置资源处理器");

        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
