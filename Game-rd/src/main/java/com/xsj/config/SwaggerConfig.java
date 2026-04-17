package com.xsj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("初始化 Swagger OpenAPI 配置");
        return new OpenAPI()
                .info(new Info()
                        .title("GameSphere API 文档")
                        .version("1.0.0")
                        .description("GameSphere 游戏社区系统后端 API 接口文档")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
