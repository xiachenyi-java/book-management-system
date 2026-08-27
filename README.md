# 小说阅读管理系统

基于 Spring Boot + Spring Data JPA + Redis + JWT 的小说内容发布与阅读后端服务，支持管理员发布小说与章节、读者在线阅读，实现了完整的用户认证、RBAC 权限控制、接口限流与缓存体系。

## 技术栈

- **框架**：Spring Boot 3.x, Spring Security, Spring Data JPA
- **数据库**：MySQL 8.0
- **缓存**：Redis（Token 黑名单、用户缓存、接口限流、双 Token 刷新）
- **安全**：JWT + BCrypt 密码加密 + 自定义注解限流
- **容器化**：Docker + Docker Compose
- **文档**：Swagger / OpenAPI 3
- **工具**：Lombok, Jakarta Validation, AOP

## 功能模块

### 1. 用户认证中心
- 用户注册 / 登录（BCrypt 密码加密）
- **双 Token 机制**：Access Token（JWT）+ Refresh Token（Redis 存储，7 天过期）
- Token 刷新接口（`/users/refresh`）
- **Token 黑名单**：登出时将 Token 写入 Redis，实现远程作废
- **接口限流**：基于 Redis 固定窗口计数器，自定义 `@RateLimit` 注解实现 IP 级限流

### 2. RBAC 权限控制
- 自定义 `@RequireAdmin` 注解 + AOP 拦截器实现接口级权限控制
- `LoginInterceptor` 拦截器解析 JWT 并注入 ThreadLocal 用户上下文
- 区分管理员与读者角色，普通用户访问管理接口返回 403

### 3. 小说内容管理（管理员）
- 创建小说（书名、简介、分类、封面）
- 发布章节（自动计算字数、更新小说总字数与最后更新时间）
- 修改章节（重新计算字数差并同步到小说总字数）
- 删除章节（扣减字数、自动重排章节序号）
- 删除小说（级联删除其下所有章节）

### 4. 读者阅读接口
- 小说分页列表（按更新时间倒序）
- 小说详情（含章节目录，正文脱敏）
- 单章阅读（返回完整正文）

### 5. 文件上传
- 本地图片上传，支持封面图存储
- 通过 `/uploads/` 路径匿名访问

### 6. 全局工程化
- 统一响应格式 `Result&lt;T&gt;`
- 全局异常处理（运行时异常、参数校验异常、限流异常 429）
- DTO / VO 分层解耦
- 参数注解式校验（用户名长度、密码强度、非空判断）
- 多环境配置（dev / prod）

## 项目结构
