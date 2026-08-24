package com.study.backend.misc;

/**
 * 夏辰义
 * 2026/8/1213:58
 */
public class 重要知识2 {
}
/*
1. 为什么用 @RestControllerAdvice 而不是 @ControllerAdvice？
@ControllerAdvice：默认返回页面（HTML），适合传统 MVC
@RestControllerAdvice：默认返回JSON，适合前后端分离
如果你用了 @ControllerAdvice，前端会收到 404 或一堆 HTML 标签，而不是 {"code":500...}。
2. ArithmeticException 会被捕获吗？
ArithmeticException（比如 1/0）不是 RuntimeException 吗？
它是！ ArithmeticException 继承自 RuntimeException，所以也会被你的处理器捕获。
但如果是编译时异常（比如 IOException、SQLException），你的 @ExceptionHandler(RuntimeException.class) 抓不到。解决办法
3. 为什么全局异常比 try-catch 好？
不遗漏：100 个接口不用写 100 个 try-catch，不会忘记
统一：错误格式由一个人（GlobalExceptionHandler）管，不会有的返回 JSON、有的返回字符串
干净：Controller 只关心正常流程，代码变短
 */
/*
📋 第 2 题：@NotBlank、@NotNull、@NotEmpty 的区别
表格
注解	能用在哪	检查内容	示例
@NotNull	任何类型	不能是 null	null ❌，"" ✅，" " ✅
@NotEmpty	String / 集合 / 数组 / Map	不能是 null，且长度 > 0	null ❌，"" ❌，" " ✅（因为有一个空格，长度=1）
@NotBlank	只能 String	不能是 null，不能是 ""，不能全是空格	null ❌，"" ❌，" " ❌，"a" ✅
你的场景用 @NotBlank 最严格、最合适，因为书名不能全是空格。
📋 第 3 题：为什么校验放在 Controller 而不是 Service？
两个理由：
越早拦截越好
空书名在 Controller 就被拦住，根本不用进 Service、不用查数据库、不用执行 SQL。如果放在 Service，意味着代码跑了一大半才发现参数有问题，浪费资源。
分层职责
Controller：负责输入校验（参数格式对不对、必填项有没有）
Service：负责业务校验（这本书是否存在、库存够不够、余额足不足）
如果把"参数不能为空"这种格式校验放到 Service，Service 代码会被一堆 if (xxx == null) 淹没，业务逻辑反而看不清。
 */