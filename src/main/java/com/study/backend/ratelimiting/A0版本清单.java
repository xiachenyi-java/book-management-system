package com.study.backend.ratelimiting;

/**
 * 夏辰义
 * 2026/8/2417:50
 */
public class A0版本清单 {
}
/*
第1步：理解限流原理（固定窗口计数器）
第2步：给登录接口加限流（防暴力破解）
第3步：升级为 AOP 注解式限流（通用、优雅）
第4步：双 Token 机制设计（Access + Refresh）
第5步：修改登录，返回双 Token，Refresh Token 存 Redis
第6步：新增 /users/refresh 接口（用 Refresh 换新的 Access）
第7步：让 Refresh Token 可远程作废（修改密码/登出时失效）
 */