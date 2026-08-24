# 图书管理系统

基于 Spring Boot + Spring Data JPA + Redis + JWT 的图书管理后端服务。

## 技术栈

- **框架**：Spring Boot, Spring Security, Spring Data JPA
- **数据库**：MySQL 8.0
- **缓存**：Redis（Token 黑名单、用户缓存、接口限流）
- **安全**：JWT + BCrypt + 接口限流
- **容器化**：Docker + Docker Compose
- **文档**：Knife4j

## 快速启动

```bash
# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 运行项目
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev