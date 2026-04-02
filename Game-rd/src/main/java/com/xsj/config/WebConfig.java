package com.xsj.config;

import com.xsj.interceptor.LogInterceptor;
import com.xsj.interceptor.UserBehaviorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;
    private final UserBehaviorInterceptor userBehaviorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );

        registry.addInterceptor(userBehaviorInterceptor)
                .addPathPatterns("/game/detail/**", "/game/click/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:D:/TapTapGameSphere-rd/Game-rd/uploads/");

        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-starter-webmvc-ui/2.3.0/");

        registry.addResourceHandler("/swagger-ui/**", "/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
