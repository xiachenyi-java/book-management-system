package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1213:58
 */
public class A3第四版思路 {
}
/*
🧯 用"大楼消防系统"理解全局异常处理
你现在的"病"：每个房间自备灭火器
你的系统是一栋大楼，每层有很多房间（Controller 方法）。以前着火了（抛异常）怎么办？
老办法：每个房间放一个小灭火器（try-catch 或手动 return 错误字符串）
java
// 房间 101：查询图书
try {
    bookService.findById(id);
} catch (Exception e) {
    return "书不存在";  // ← 手动灭火
}

// 房间 102：添加图书
try {
    bookService.add(name);
} catch (Exception e) {
    return "添加失败";  // ← 再手动灭一次
}
问题：
100 个房间就要写 100 次，累
有的房间忘了放灭火器，火就烧穿楼顶，Tomcat 吐出一堆 HTML 错误页给前端
每个房间灭火方式不一样：有的返回字符串，有的返回 JSON，有的直接让前端卡死
新办法：大楼装总控喷淋系统
在大堂装一个中央消防控制器（GlobalExceptionHandler），无论哪个房间着火，自动喷水，统一扑灭。
java
@RestControllerAdvice  // ← 我是大楼消防总控
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)  // ← 只要闻到烟味（RuntimeException）
    public Result<Void> handle(RuntimeException e) {
        return Result.error(e.getMessage());   // ← 统一喷水（返回标准错误 JSON）
    }
}
好处：
不漏：100 个房间不用逐个检查，总控全覆盖
统一：所有火灾都喷同一种水（同一种 JSON 格式）
干净：房间里的人（Controller）专心干活，不用背灭火器
🏗️ GlobalExceptionHandler 的解剖
三个核心零件
表格
零件	代码	作用
消防总控开关	@RestControllerAdvice	声明"我是大楼消防中心"，监听所有 Controller
烟雾传感器	@ExceptionHandler(RuntimeException.class)	声明"只要出现 RuntimeException，就触发我"
喷淋头	Result.error(e.getMessage())	把异常包装成标准信封，返回给前端
为什么用 @RestControllerAdvice 而不是 @ControllerAdvice？
表格
注解	返回什么	适用场景
@ControllerAdvice	页面（HTML）	传统网站，后端渲染页面
@RestControllerAdvice	JSON	前后端分离，前端要数据
如果你用了 @ControllerAdvice，前端收到的不是 {"code":500...}，而是一堆 HTML 标签，前端解析直接报错。
🌊 解剖一次"查不到书"的新数据流
老流程（没装消防系统）
plain
前端请求 /books/99999
    ↓
Controller 调 Service.findById(99999)
    ↓
Service 抛 RuntimeException("书不存在")
    ↓
没人管 → Tomcat 接手 → 返回 HTML 错误页
    ↓
前端收到 <html><body><h1>Error...</h1>... 一脸懵逼
新流程（装了消防系统）
plain
前端请求 /books/99999
    ↓
Controller 调 Service.findById(99999)
    ↓
Service 抛 RuntimeException("书不存在")
    ↓
GlobalExceptionHandler 捕获
    ↓
包装成 Result.error("书不存在")
    ↓
前端收到 {"code":500,"msg":"书不存在","data":null}
    ↓
前端判断 res.code === 500，弹出红框提示

 */