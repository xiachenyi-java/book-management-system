package com.example2.demo2.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 夏辰义
 * 2026/8/2121:24
 */


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")                    // 拦截所有
                .excludePathPatterns(                      // 排除以下（不用登录就能访问）
                        "/users/login",                        // 登录接口
                        "/users/register",                     // 注册接口
                        "/swagger-ui/**",                      // Swagger 页面
                        "/swagger-ui.html",                    // Swagger 首页（有些版本是这个）
                        "/v3/api-docs/**",                     // Swagger API 数据
                        "/error",                               // Spring 错误页
                        "/",                    // 加这行：根路径
                        "/index.html",          // 加这行：首页
                        "/*.html",              // 加这行：所有静态页面
                        "/favicon.ico"          // 加这行：图标
                );
    }
}