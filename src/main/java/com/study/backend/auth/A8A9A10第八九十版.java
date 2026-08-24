package com.study.backend.auth;

/**
 * 夏辰义
 * 2026/8/2122:17
 */
public class A8A9A10第八九十版 {
}
/*
一句话串联：CORS 解决"浏览器让不让进"的问题 → 登录用户解决"你是谁"的问题 → RBAC 解决"你能干什么"的问题。
8. 跨域配置（CORS）
核心概念
同源策略：浏览器默认禁止不同源（协议、域名、端口任一不同）的页面间请求资源。
CORS（Cross-Origin Resource Sharing）：W3C 标准，允许服务器声明哪些源可以访问其资源。
关键响应头
表格
响应头	作用
Access-Control-Allow-Origin	允许的源（如 * 或 https://example.com）
Access-Control-Allow-Methods	允许的 HTTP 方法（GET/POST/PUT/DELETE 等）
Access-Control-Allow-Headers	允许的请求头（如 Content-Type、Authorization）
Access-Control-Allow-Credentials	是否允许携带 Cookie（设为 true 时 Origin 不能为 *）
Access-Control-Max-Age	预检请求缓存时间
预检请求（Preflight）
对于非简单请求（如 PUT、DELETE、自定义头），浏览器会先发送 OPTIONS 请求试探，通过后才会发真实请求。
实践建议
生产环境不要用 *，应精确配置白名单。
需要 Cookie 时，必须同时设置 Allow-Credentials: true 和具体 Origin。
网关层（Nginx/网关）统一配置 CORS，避免每个服务重复处理。
9. 获取当前登录用户
核心逻辑
用户身份识别通常依赖凭证，常见方式：
表格
方式	原理	获取方式
Session	服务端存状态，Cookie 传 Session ID	request.getSession().getAttribute("user")
JWT Token	客户端存 Token，每次请求携带	从 Authorization Header 解析 Token 载荷
OAuth2 / SSO	第三方或统一认证中心签发凭证	校验 Token 后换取用户信息
通用实现步骤
登录时：校验用户名密码 → 生成凭证（Session/JWT）→ 返回给客户端。
请求时：客户端携带凭证（Cookie / Header）。
拦截器中：解析凭证 → 查询/反解用户信息 → 存入线程上下文（如 ThreadLocal）。
业务层：从上下文中直接取当前用户，避免每个接口手动传参。
线程安全注意
用 ThreadLocal 存当前用户时，务必在请求结束时 remove()，防止线程池复用导致数据泄漏。
10. 接口权限分级（RBAC 初探）
RBAC（Role-Based Access Control）模型
plain
用户(User) ←→ 角色(Role) ←→ 权限(Permission) ←→ 资源(Resource)
用户绑定角色，角色拥有权限，权限对应具体的资源/操作。
解耦"人"和"权"：调整角色即可批量变更权限，无需逐个改用户。
权限设计的两个维度
表格
维度	说明	示例
菜单权限	前端可见性	是否显示"用户管理"菜单
接口权限	后端访问控制	是否能调用 DELETE /api/users
接口层实现思路
定义权限标识：如 user:read、user:delete、order:write。
注解标记：在 Controller 方法上加自定义注解，如 @RequirePermission("user:delete")。
拦截器/切面：请求进入时，解析当前用户的角色 → 查其权限列表 → 比对方法所需权限。
无权限时：返回 403 Forbidden。
 */