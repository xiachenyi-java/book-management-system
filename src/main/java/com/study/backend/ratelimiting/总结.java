package com.study.backend.ratelimiting;

/**
 * 夏辰义
 * 2026/8/2513:12
 */
public class 总结 {
}
/*
这是一个从 接口安全加固 到 认证体系升级 的完整链路，7 步环环相扣。以下是结构化总结：
整体脉络
plain
限流防刷（1~3步） → 双 Token 认证（4~6步） → Token 生命周期管理（7步）
各步核心要点
第 1 步：固定窗口计数器
表格
项目	内容
原理	把时间切分为固定窗口（如 1 分钟），每个窗口内允许 N 次请求，超出的拒绝
优点	实现简单，内存占用低
缺点	窗口边界可能突发 2N 次请求（临界突刺问题）
适用	登录、注册等对精度要求不高的场景
第 2 步：给登录接口加限流
目的：防止暴力破解密码
做法：登录前检查同一 IP / 同一用户名在窗口期内请求次数
效果：连续输错密码 N 次后暂时封禁
第 3 步：升级为 AOP 注解式限流
问题：第 2 步的限流逻辑写在登录方法里，复用性差
解决：自定义 @RateLimit 注解 + AOP 切面
好处：
任何接口加一行注解就能限流
限流逻辑与业务代码解耦
支持配置窗口大小、阈值、限流维度（IP / 用户 / 全局）
第 4 步：双 Token 机制设计
表格
Token 类型	有效期	存储位置	用途
Access Token	短（如 30 分钟）	客户端内存	访问受保护接口
Refresh Token	长（如 7 天）	Redis + 客户端	Access Token 过期后换新的
为什么不用单 Token 长有效期？ 被盗后风险窗口太大
为什么 Access Token 要短？ 减少被盗后的可用时间
Refresh Token 为什么存 Redis？ 服务端可控，能远程作废
第 5 步：修改登录，返回双 Token
登录成功时：
生成 JWT 格式的 accessToken（含 userId、username、role）
生成 UUID 格式的 refreshToken
refreshToken 写入 Redis：refresh:{userId} → {refreshToken}，设置过期时间
返回给客户端：{ accessToken, refreshToken, userInfo }
第 6 步：新增 /users/refresh 接口
流程：
plain
客户端带 refreshToken → 服务端去 Redis 反查 userId
                              ↓
                    比对 Redis 里的值是否一致
                              ↓
                    ✅ 一致：删除旧 refreshToken
                              ↓
                    生成新的 accessToken + 新的 refreshToken
                              ↓
                    新 refreshToken 写入 Redis
                              ↓
                    返回 { newAccessToken, newRefreshToken }
关键修复点（你踩过的坑）：
refresh 接口本身不能被 JWT 过滤器拦截（否则 accessToken 过期后永远刷不了）
refreshToken 是 UUID，不能当 JWT 解析
建议做 Refresh Token 轮换（每次刷新都换新的，旧的作废）
第 7 步：Refresh Token 可远程作废
场景：
表格
场景	操作
用户修改密码	删除 Redis 里该用户的 refresh:{userId}
用户登出	同上，并清除客户端本地 token
管理员强制下线	直接删 Redis 键
实现：
因为 refreshToken 存在 Redis，删了就行
已颁发的 accessToken 不用管，等它自己过期（短时效）
如果需要立刻让 accessToken 也失效，需要引入 黑名单机制（Redis 存 blacklist:{jti}）
最终架构图
plain
┌─────────────┐     登录      ┌─────────────┐
│   客户端     │ ────────────→ │  登录接口    │
│             │               │  + @RateLimit│
└─────────────┘               └──────┬──────┘
       ↑                             │
       │              ┌──────────────┘
       │              ↓
       │      ┌─────────────┐
       │      │ 生成双 Token │
       │      │ Access: JWT │
       │      │ Refresh: UUID → Redis
       │      └──────┬──────┘
       │              ↓
       └──────────────┘
              返回 {accessToken, refreshToken}

       ┌─────────────────────────────────────┐
       │  正常请求：Header 带 Access Token    │
       │  过期后：POST /users/refresh         │
       │         带 Refresh Token → 换新的    │
       └─────────────────────────────────────┘
一句话总结
先用 限流 挡住暴力攻击，再用 双 Token 实现"短时效高安全 + 长会话免登录"，最后通过 Redis 存储 + 远程删除 让 Token 生命周期完全可控。
 */