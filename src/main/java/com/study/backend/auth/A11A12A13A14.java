package com.study.backend.auth;

/**
 * 夏辰义
 * 2026/8/2315:54
 */
public class A11A12A13A14 {
}
/*
11. Token 刷新与登出
表格
要点	说明
Token 刷新	双 Token 策略（Access Token + Refresh Token），Access Token 短期（如 15 分钟），Refresh Token 长期（如 7 天）
刷新时机	客户端在 Access Token 即将过期或收到 401 时，携带 Refresh Token 调用 /auth/refresh
登出逻辑	服务端将 Token 加入黑名单（Redis 存储），或使 Refresh Token 失效
安全考虑	Refresh Token 轮转（每次刷新生成新的 Refresh Token，旧的作废）、单设备登录限制
12. 登录相关单元测试
表格
测试维度	覆盖点
正常流程	正确用户名密码 → 返回 Token；Token 刷新成功
异常流程	密码错误、用户不存在、账号锁定、Token 过期/伪造
边界条件	空输入、超长输入、SQL 注入/XSS 尝试
安全测试	暴力破解防护（登录失败次数限制）、验证码校验
依赖 Mock	数据库、Redis、邮件/短信服务、外部认证源
13. 登录 API 文档完善
表格
文档要素	内容
接口定义	请求/响应格式、HTTP 方法、URL、Content-Type
字段说明	每个字段的类型、是否必填、取值范围、示例值
错误码	统一错误码体系（如 1001 密码错误、1002 账号锁定）
安全说明	HTTPS 强制、Token 传递方式（Header Authorization: Bearer <token>）、CORS 策略
示例	cURL 示例、各语言调用示例
14. 敏感配置外部化
表格
配置项	外部化方式
数据库密码	环境变量 / 密钥管理服务（AWS Secrets Manager、阿里云 KMS）
JWT Secret	环境变量，生产环境定期轮换
第三方密钥	OAuth Client Secret、短信/邮件 API Key
加密盐值	独立配置，与代码分离
实现方式	.env 文件（开发）→ K8s Secret / 配置中心（生产），禁止硬编码
15. Docker 打包
表格
要点	最佳实践
基础镜像	优先使用官方精简镜像（如 node:20-alpine、python:3.11-slim、eclipse-temurin:17-jre-alpine），减少攻击面和体积
多阶段构建	分离构建环境（含编译工具）和运行环境（仅运行时依赖），显著减小最终镜像体积
非 root 运行	创建专用用户/用户组（如 appuser），避免容器以 root 身份运行
敏感信息	通过 docker run -e 或 docker-compose 环境变量注入，绝不写入镜像层
健康检查	添加 HEALTHCHECK 指令，配合 K8s / Docker Swarm 实现自动故障恢复
镜像标签	使用语义化版本标签（如 v1.2.3），避免只用 latest 导致回滚困难
.dockerignore	排除 .git、node_modules、测试文件、本地配置文件，加速构建并减少层大小
 */