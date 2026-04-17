package com.xsj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/file/avatar/**")
                .addResourceLocations("file:" + uploadPath + "avatar/");
        registry.addResourceHandler("/api/file/chat/**")
                .addResourceLocations("file:" + uploadPath + "chat/");
    }
}
