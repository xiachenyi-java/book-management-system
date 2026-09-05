package com.study.backend.Asynchronous;

/**
 * 夏辰义
 * 2026/9/418:09
 */
public class A0版本清单 {
}
/*
□ 第1步：用 Redis Hash 重构 Token 黑名单（替代 String，理解数据结构选型）
□ 第2步：给小说详情加缓存（@Cacheable + CacheConfig，解决缓存一致性）
□ 第3步：用 Redis ZSet 实现"热门小说排行榜"（按阅读量排序）
□ 第4步：用 Redis 分布式锁实现"用户签到"（防并发重复，理解 SETNX）
□ 第5步：引入 RabbitMQ，理解 Exchange + Queue + RoutingKey
□ 第6步：发布新章节时，发一条 MQ 消息（异步通知订阅用户）
□ 第7步：给 MQ 消费加死信队列（消息失败不丢失，理解可靠性）
□ 第8步：用 Redisson 替换手写 Redis 分布式锁（看门狗自动续期）
 */