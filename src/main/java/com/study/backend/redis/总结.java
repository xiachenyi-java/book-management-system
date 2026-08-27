package com.study.backend.redis;

/**
 * 夏辰义
 * 2026/8/2513:12
 */
public class 总结 {
}
/*
这是一个从 "连上 Redis" 到 "用好 Redis" 的渐进式集成路线，6 步由浅入深。
整体脉络
plain
基础设施（1~2步） → 解决安全痛点（3步） → 性能优化（4~5步） → 工程化收尾（6步）
各步核心要点
第 1 步：在你的电脑上跑起来 Redis
表格
方式	命令/操作
Windows	运行 redis-server.exe redis.windows.conf
Linux/Mac	redis-server 或 brew services start redis
Docker	docker run -d -p 6379:6379 --name redis redis:latest
验证	redis-cli ping → 返回 PONG
这是前置条件，Redis 没启动，后面所有步骤都报错。
第 2 步：让 Spring Boot 连上 Redis
加依赖：spring-boot-starter-data-redis
配 YAML：
yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
注入使用：StringRedisTemplate（推荐，存字符串）或 RedisTemplate
验证：写个测试类 opsForValue().set("test", "ok")，Redis 里能看到就说明通了。
第 3 步：让"登出"真正生效（Token 黑名单）← 最快见效
问题：JWT 是无状态的，服务端没法让已颁发的 Access Token "失效"
解决：登出时提取 Access Token 的 jti（唯一标识），写入 Redis 黑名单：
java
// 登出时
stringRedisTemplate.opsForSet().add("blacklist:jti", jti);
stringRedisTemplate.expire("blacklist:jti", remainingTime, TimeUnit.SECONDS);
校验时：JWT 过滤器先查黑名单，命中则拒绝：
java
if (stringRedisTemplate.opsForSet().isMember("blacklist:jti", jti)) {
    throw new RuntimeException("Token 已注销");
}
为什么最快见效：前面你已经用 Redis 存了 Refresh Token，黑名单是同一套思路的延伸，改动最小。
第 4 步：缓存当前登录用户信息
问题：每个请求都要从数据库查用户信息（GET /书/我 等接口）
解决：登录成功后把 User 对象 JSON 序列化后存 Redis：
java
stringRedisTemplate.opsForValue().set(
    "user:" + userId,
    JSON.toJSONString(user),
    30, TimeUnit.MINUTES
);
效果：后续接口直接从 Redis 读，零 SQL
注意：用户修改信息时要 更新或删除缓存（保证一致性）
第 5 步：缓存图书列表（学习 @Cacheable）
问题：图书列表读多写少，每次都查数据库浪费资源
解决：用 Spring Cache 注解，代码侵入最小：
java
@Cacheable(value = "books", key = "'list'")
public List<Book> getBookList() {
    return bookRepository.findAll();  // 只执行一次，后续走缓存
}

@CacheEvict(value = "books", key = "'list'")
public void addBook(Book book) {
    bookRepository.save(book);  // 增删改时清缓存
}
效果：图书接口响应速度提升，数据库压力下降
第 6 步：配置 JSON 序列化（解决乱码问题）
问题：默认 RedisTemplate 用 JDK 序列化，Redis 里看到的是 \xac\xed\x00\x05 乱码
解决：配置 Jackson JSON 序列化器：
java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
}
效果：Redis 可视化工具里能看到明文 JSON，方便调试
最终架构图
plain
┌─────────────┐
│   Redis     │
│  服务启动   │
└──────┬──────┘
       ↓
┌─────────────┐     ┌─────────────────────────────┐
│ Spring Boot │ ←── │ 连接配置（host/port）        │
│  集成 Redis  │     └─────────────────────────────┘
└──────┬──────┘
       ↓
┌─────────────────────────────────────────────┐
│  实际应用场景（由急到缓，由安全到性能）        │
│                                             │
│  ① Token 黑名单（登出生效）                  │
│  ② Refresh Token 存储（双Token刷新）         │
│  ③ 用户信息缓存（减少DB查询）                  │
│  ④ 图书列表缓存（@Cacheable）                │
│                                             │
│  ⑤ JSON 序列化（工程化，肉眼可读）             │
└─────────────────────────────────────────────┘
一句话总结
先 启动并连通 Redis，再用 黑名单 解决登出痛点，接着用 缓存 减少数据库查询，最后配好 JSON 序列化 让一切整洁可维护。
 */