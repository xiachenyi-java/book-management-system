package com.study.backend.codequality;

/**
 * 夏辰义
 * 2026/9/322:54
 */
public class A0版本清单 {
}
/*
□ 第1步：把 UserService.refresh() 的 KEYS 命令改成 Hash/Scan（消除 Redis 生产环境阻塞）
□ 第2步：给 FileController 加文件大小限制 + 路径遍历校验（防上传攻击）
□ 第3步：把 UserService.login() 的重复查询改成单次查询（性能意识）
□ 第4步：统一自定义业务异常（替代 RuntimeException，方便日志分级）
□ 第5步：给 NovelService 的 @Transactional 加 rollbackFor 显式声明
□ 第6步：把 ObjectMapper 改成 Spring 注入（删掉 tools 包，理解自动配置）
□ 第7步：给 Reader 接口（GET /novels/**）加白名单或独立权限注解（修复权限漏洞）
□ 第8步：提取配置常量（把硬编码的 localhost:8080 移到 application.yml）
□ 第9步：给 NovelService 加接口层（抽离 Controller 里的业务逻辑到 Service）
 */