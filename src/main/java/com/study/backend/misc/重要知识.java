package com.study.backend.misc;

/**
 * 夏辰义
 * 2026/8/1022:40
 */
public class 重要知识 {
}
/*
问题 1：为什么 Controller 不能直接 new BookService()？
答案：因为 BookService 里面需要 BookRepository，而 BookRepository 是 Spring 自动生成的代理对象
（里面封装了数据库连接、事务、SQL 生成）。你自己 new 出来的 BookService，里面的 bookRepository 是 null，调用就会空指针。
Spring 的本质：它是一个"高级工厂"，你告诉它"我要这个对象"，它把已经配置好依赖关系的对象塞给你。这叫控制反转（IoC）
问题 2：为什么 Service 不能反过来调用 Controller？
如果厨师（Service）想通知顾客，他不能冲出厨房直接找顾客，他必须让服务员（Controller）去传话。
分层架构的铁律：上层可以调下层，下层绝对不允许调上层。否则层与层之间缠成一团，改一个地方全崩。
问题 3：Entity 为什么叫"实体"，而不叫"数据库表"？
因为 Book 这个类不代表数据库表本身，它代表"一本书这个业务概念"。只是 JPA 把它映射到了表上。
以后你会遇到：数据库表名叫 t_book，但 Entity 类名叫 Book；或者数据库字段叫 create_time，
但 Entity 字段叫 createdAt。这就是解耦——代码里的名字跟着业务走，数据库里的名字跟着 DBA 规范走。
 */

/*
思考题 1：为什么 success() 和 error() 要写成 static？
先假设不写 static
如果 success 不是静态的，那你每次用的时候必须先 new 一个对象
success() 明明和具体哪个 Result 对象没关系，它只是一个"造信封的工厂"
但你却必须先 new 一个对象才能调用，这个对象本身毫无意义
而且 temp 的类型是 Result<String>，但返回的却是 Result<List<Book>>，逻辑上很别扭
写成 static 之后
好处：
不需要先 new：这是"工厂方法"的核心思想，我直接通过类名就能生产一个标准品
语义清晰：Result.success(...) 读出来就是"我要一个成功的结果"，而不是"我先造一个结果，再把它变成成功状态"
强制规范：你无法在外部 new Result<>() 然后乱填 code 和 msg，只能通过我提供的工厂方法，这就保证了"成功一定是 200，失败一定是 500"
一句话总结：static 表示这个方法属于"Result 这个概念"，而不属于"某一个 Result 对象"。
思考题 2：前端不传 name，@RequestParam String name 会报什么错？
先理解 @RequestParam 的工作方式
@RequestParam 的意思是："必须从请求里拿到这个参数，拿不到就报错"。
前端传参有两种方式：
URL 问号后面：POST /books?name=西游记
请求体（Body）：{"name":"西游记"}
@RequestParam 只认问号后面的参数。
如果前端没传 name
后端会抛出异常：
Spring 会自动把这个异常包装成 HTTP 响应，返回给前端的是：
HTTP 状态码：400 Bad Request
响应体：可能是一段 HTML 错误页，或者一段 JSON（取决于 Spring 版本）
为什么状态码是 400？
因为 HTTP 协议规定：
400 = 客户端请求有语法错误或参数缺失（是你前端没传参数，后端拒绝处理）
500 = 服务器内部错误（是后端代码出 bug 了）
所以这是前端的锅，不是后端的锅，因此是 400。
对比：如果不写 @RequestParam 会怎样？
不写 @RequestParam，Spring 也会尝试从 URL 参数里取，但取不到不会报错，name 会变成 null。然后你的代码继续执行，把 null 存进数据库，或者后面某个地方空指针。
所以 @RequestParam 的作用是：提前拦截，让错误在入口处就暴露出来，而不是埋到数据库里。
思考题 3：前端搜索 10 万本书会怎样？
你现在的搜索逻辑
思考题 3：前端搜索 10 万本书会怎样？
你现在的搜索逻辑
会发生什么？
1. 网络层面
后端 findAll() 查 10 万条数据，生成巨大的 JSON（可能几十 MB）
浏览器下载这几十 MB，用户的流量在燃烧，等待时间可能几十秒
2. 浏览器层面
JavaScript 把这 10 万条数据加载进内存
filter() 遍历 10 万次，浏览器标签页卡死，甚至弹出"页面无响应"
3. 用户体验
搜索框输入"西"，等 30 秒才出结果
手机用户直接闪退
正确的做法应该是什么？
后端搜索：前端只传一个 keyword，后端在数据库里用 LIKE '%keyword%' 查，只返回匹配的 10 条。
分页：即使搜索结果有 1000 条，也分 20 页显示，每页只传 5 条。
 */