package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1322:00
 */
public class A6第七版思路 {
}
/*
📋 用"访客登记表"理解 DTO
你现在的"病"：让访客直接填档案
以前，前端传数据，后端直接用 Book 实体接收：
java
public Result<Book> add(@RequestBody @Valid Book book)
Book 是什么？是数据库档案，里面有 id、name、displayOrder。就像公司的人事档案柜，记录着员工的完整信息。
问题：你让访客（前端）直接填人事档案。访客本来只该填"姓名"，但你把档案摊在他面前，他顺手把"工号"、"入职日期"也填了，甚至乱填。
新办法：只给访客一张登记表
java
public Result<Book> add(@RequestBody @Valid BookDTO dto)
BookDTO 是什么？是访客登记表，上面只有一行：
姓名：________
访客填不了工号，改不了入职日期，只能填你该填的。
🏗️ DTO 的解剖
为什么叫 Data Transfer Object？
表格
单词	含义	对应你的代码
Data	数据	前端传过来的书名
Transfer	传输	从前端传到后端的过程
Object	对象	BookDTO 这个 Java 类
核心作用：专门负责"前端 → 后端"这段路上的数据搬运，和数据库无关。
你的 DTO 长什么样
java
@Data
public class BookDTO {
    @NotBlank(message = "书名不能为空")
    private String name;
}
注意它没有什么：
❌ 没有 id（数据库 ID 不该由前端决定）
❌ 没有 displayOrder（序号是后端业务算的）
❌ 没有 @Entity、@Table（它不是数据库表）
改造后的数据流
plain
前端传 {"name": "西游记"}
    ↓
Controller 用 BookDTO 接收 → Spring 自动解析，只认 name 字段
    ↓
dto.getName() → 取出书名
    ↓
Service 计算 displayOrder，创建 Book 实体
    ↓
Repository 把 Book 实体存入数据库
前端永远碰不到 Book 实体，只能碰到 BookDTO。
 */