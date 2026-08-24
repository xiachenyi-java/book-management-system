package com.study.backend.auth;

/**
 * 夏辰义
 * 2026/8/2121:54
 */
public class A6A7第五六版 {
}
/*
6. 登录接口对外暴露
表格
要点	说明
白名单配置	登录接口必须加入拦截器白名单（excludePaths），否则 JWT 校验会拦截登录请求本身，导致死循环
安全性加固	虽对外暴露，但需配套：验证码/图形验证、登录失败次数限制（防暴力破解）、IP 限流、密码加密传输（HTTPS）
多端适配	若支持 Web/App/小程序等多端登录，建议统一登录接口或按端分路径（如 /api/auth/login）
返回值规范	登录成功后返回 JWT Token（Access Token + Refresh Token 可选），失败返回统一错误码，禁止暴露敏感信息
7. 登录拦截器（JWT 校验）
表格
要点	说明
拦截范围	拦截所有业务请求，排除白名单接口（登录、注册、公开资源等）
Token 提取	从请求头 Authorization: Bearer <token> 中提取 JWT，需兼容大小写及空格
校验逻辑	① 解析 Token 结构 ② 验证签名（防篡改）③ 检查过期时间（exp）④ 可选：校验 Token 黑名单（登出场景）
上下文注入	校验通过后，将用户 ID、角色等信息写入 ThreadLocal / RequestContext，供后续业务层使用
异常处理	Token 缺失/过期/非法时，统一返回 401 Unauthorized，不要抛 500 暴露内部细节
Refresh 机制	Access Token 有效期建议短（15~30 分钟），Refresh Token 可较长（7~30 天），在拦截器中实现自动续期或返回特定码让前端刷新
两者关系示意
plain
请求 → 拦截器
        │
        ├─ 路径在白名单？（如 /login）→ 放行，执行登录逻辑，返回 Token
        │
        └─ 不在白名单 → 提取 Header 中的 JWT → 校验通过 → 注入用户上下文 → 放行
                              ↓
                        校验失败 → 返回 401
 */