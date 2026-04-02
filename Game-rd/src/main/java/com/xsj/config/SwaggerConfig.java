package com.xsj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:GameSphere}")
    private String appName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName + " API 文档")
                        .version("1.0.0")
                        .description("GameSphere 游戏社区系统后端 API 接口文档")
                        .contact(new Contact()
                                .name("GameSphere Team")
                                .email("support@gamesphere.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082/api")
                                .description("本地开发环境"),
                        new Server()
                                .url("http://localhost:8082/api")
                                .description("测试环境")))
                .schemaRequirement("Bearer Authentication", createSecurityScheme())
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("请输入 JWT Token，格式为：Bearer {token}");
    }
}
