package com.study.backend.auth;

/**
 * 夏辰义
 * 2026/8/2116:21
 */
public class A5第五版 {
}
/*
JWT 令牌机制总结
用"游乐园通票"的比喻来理解这段代码：
核心问题：登录后，服务器怎么"记住"你是谁？
想象你登录了一个 App，之后每次点按钮都要重新输密码吗？
❌ 传统 Session = 服务器给每个人建一个小柜子存身份证，你每次来报柜号（Cookie 里的 SessionID），服务器去翻柜子查你是谁。
缺点：服务器压力大，多台服务器之间柜子不同步（A 机器认识你，B 机器不认识）。
✅ JWT = 服务器给你一张自带防伪的通票，你自己揣兜里，每次请求都出示。服务器不用存任何信息，看一眼票就知道你是谁、票是不是真的、有没有过期。
JWT 通票长什么样？
一张 JWT 票由三部分组成（用点号隔开）：
plain
xxxxx.yyyyy.zzzzz
  ↑      ↑      ↑
Header  Payload  Signature
（票面格式）（个人信息）（防伪章）
表格
部分	代码对应	票面上的体现
Header	Jwts.builder() 默认生成	"本票采用 HS256 防伪技术"
Payload	.subject() / .claim()	"游客编号：1001，姓名：小明，签发日期：今天，有效期：7天"
Signature	.signWith(getKey())	票底的镭射防伪章，只有服务器知道怎么印
代码拆解：印票与检票
1️⃣ 印票：generateToken() — 售票处
java
public String generateToken(Integer userId, String username) {
    return Jwts.builder()
        .subject(String.valueOf(userId))      // 票上印：游客编号
        .claim("username", username)          // 票上印：游客姓名
        .issuedAt(now)                        // 票上印：今天发行
        .expiration(expiryDate)               // 票上印：7天后过期
        .signWith(getKey(), Jwts.SIG.HS256)   // 盖上防伪镭射章
        .compact();                           // 压膜成一张完整的票
}
关键点：
SECRET = 印钞厂的母版，只有服务器知道，泄露了谁都能造假票
signWith = 用母版盖防伪章，票的内容一旦被篡改，章就对不上
expiration = 票有有效期，捡到你旧票也没用
2️⃣ 检票：parseToken() — 检票口
java
public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(getKey())         // 拿出母版比对防伪章
        .build()
        .parseSignedClaims(token)     // 验章 + 读信息（假票或过期的直接抛异常）
        .getPayload();                // 读取票面上的个人信息
}
关键点：
检票口不用去数据库查你是谁，看一眼票就全知道
如果票被涂改过、或者过期了、或者是假票，直接抛异常打回
3️⃣ 取信息：从票面上读游客编号
java
public Integer getUserIdFromToken(String token) {
    Claims claims = parseToken(token);
    return Integer.valueOf(claims.getSubject());
}
就像检票员从票面上直接看到："游客编号 1001"。
4️⃣ 返回前端：LoginVO — 把票和游客卡一起给你
java
public class LoginVO {
    private String token;      // 你的通票（以后每次请求都带上）
    private User userInfo;     // 你的基本信息（前端直接展示用）
}
登录成功后，后端把票和游客信息卡一起交给前端：
前端把 token 存到 LocalStorage，以后每次请求在 Header 里带上
前端把 userInfo 展示在页面右上角（"欢迎，小明"）
完整登录后流程
plain
用户登录成功
    ↓
后端生成 JWT 通票（generateToken）
    ↓
把 {token, userInfo} 装进 LoginVO 返回前端
    ↓
前端保存 token
    ↓
用户访问"我的订单"
    ↓
前端在请求头里带上：Authorization: Bearer <token>
    ↓
后端检票口验票（parseToken）
    ↓
验票通过 → 知道你是用户1001 → 返回订单数据
一句话总结
表格
环节	代码对应	游乐园通票理解
生成令牌	generateToken()	售票处根据你的身份印一张带防伪的通票
签名防伪	signWith(getKey())	盖唯一的镭射防伪章，假票/涂改票一眼识破
存身份信息	.subject() / .claim()	票面上印：游客编号、姓名
有效期	.expiration()	票上印截止日期，过期自动作废
解析验票	parseToken()	检票口用母版验防伪章，真票放行，假票拦下
读取身份	getUserIdFromToken()	从票面上直接读取游客编号
返回前端	LoginVO	把门票 + 游客信息卡一起交给客户
JWT 的核心优势：服务器变成"无状态"的检票口，不用给每个人建小柜子（不存 Session），你只要出示自己的通票，我验一下防伪就知道你是谁。多台服务器之间天然同步，因为票在你手里，不是存在我柜子里。
 */