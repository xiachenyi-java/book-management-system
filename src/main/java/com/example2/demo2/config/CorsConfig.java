package com.example2.demo2.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 夏辰义
 * 2026/8/2217:05
 */
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                    // 对所有路径生效
                .allowedOrigins("http://localhost:5173")  // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的 HTTP 方法
                .allowedHeaders("*")                      // 允许所有请求头（包括 Authorization）
                .allowCredentials(true)                   // 允许携带 Cookie（如果后续需要）
                .maxAge(3600);                            // 预检请求缓存 1 小时，减少 OPTIONS 次数
    }
}
