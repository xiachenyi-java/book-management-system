package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1314:52
 */
public class A5第六版思路 {
}
/*
🛂 用"机场安检"理解参数校验
你现在的"病"：坏人已经上飞机了才被发现
以前，前端传一个空书名，后端照单全收：
前端输入框留空 → 点添加
后端 BookService.add("") 继续执行
数据库 name 字段是 nullable = false → SQL 报错 → 前端收到 500 错误
问题：错误发现得太晚了。就像恐怖分子已经上了飞机，飞到一半才被发现，代价太大。
新办法：在登机口设安检门
我们要在数据进入系统的第一道门（Controller）就拦住不合法的数据：
空书名 → 安检门报警 → 直接拒绝登机 → 前端立刻收到"书名不能为空"
根本不用进厨房（Service），更不用开冰箱（数据库）
🏗️ 三层校验体系
表格
层级	作用	你的代码
前端校验	提升用户体验，减少无效请求	if (!name) return
Controller 校验	拦截非法数据，保护后端业务	@Valid + @NotBlank
数据库校验	最后一道防线，物理约束	@Column(nullable = false)
关键原则：前端校验可以被绕过（比如用 Postman 直接调接口），所以后端校验绝对不能省。
🔑 核心知识点拆解
1. @NotBlank —— 安检规则
java
@NotBlank(message = "书名不能为空")
@Column(nullable = false)
private String name;
@NotBlank 检查三个东西：
不能是 null
不能是空字符串 ""
不能全是空格 "   "
message = "..."：安检门报警时喊的话，会被异常捕获后传给前端。
2. @Valid —— 启动安检开关
java
public Result<Book> add(@RequestBody @Valid Book book)
@RequestBody：把前端 JSON 转成 Book 对象
@Valid：启动校验，Spring 会自动检查 Book 对象里所有带 @NotBlank 的字段
没有 @Valid：Spring 看到 @NotBlank 也不会检查，直接放行。
3. MethodArgumentNotValidException —— 安检报警信号
当 @Valid 发现字段不合法时，Spring 会抛出这个异常。
你的 GlobalExceptionHandler 就是"报警处理中心"：
java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<Void> handleValid(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult()
                   .getFieldErrors()
                   .get(0)
                   .getDefaultMessage();
    return Result.error(msg);  // "书名不能为空"
}
拆解：
e.getBindingResult() → 拿到安检报告
.getFieldErrors() → 所有违规字段列表
.get(0) → 取第一个违规（通常只有一个）
.getDefaultMessage() → 拿到 @NotBlank(message = "...") 里的文字
 */