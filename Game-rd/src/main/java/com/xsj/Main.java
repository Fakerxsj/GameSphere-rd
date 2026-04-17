package com.xsj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(scanBasePackages = {"com.xsj"})
@EnableAsync
@EnableScheduling
@MapperScan("com.xsj.mapper")
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(Main.class, args);
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("========================================");
            System.out.println("    GameSphere 游戏社区系统启动成功！");
            System.out.println("========================================");
            System.out.println("本地访问地址：http://localhost:" + port + contextPath);
            System.out.println("网络访问地址：http://" + hostAddress + ":" + port + contextPath);
            System.out.println("Swagger 文档：http://localhost:" + port + contextPath + "/swagger-ui/index.html");
            System.out.println("API 文档：http://localhost:" + port + contextPath + "/v3/api-docs");
            System.out.println("========================================");
        } catch (UnknownHostException e) {
            System.out.println("========================================");
            System.out.println("    GameSphere 游戏社区系统启动成功！");
            System.out.println("========================================");
            System.out.println("Swagger 文档地址：http://localhost:" + port + contextPath + "/swagger-ui/index.html");
            System.out.println("API 文档：http://localhost:" + port + contextPath + "/v3/api-docs");
            System.out.println("========================================");
        }
    }
}
