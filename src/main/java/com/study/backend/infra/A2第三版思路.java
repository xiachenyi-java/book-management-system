package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1200:09
 */
public class A2第三版思路 {
}
/*
📦 用"快递包裹"理解统一返回格式
你现在的"病"：前端每次收快递，包裹形状都不一样
表格
接口	后端发出的包裹	前端收到什么
GET /books	一摞书，直接扔过来	[{"id":1,"name":"西游记"...}]
POST /books	一张纸条	"添加成功，ID=5"
DELETE /books	一张纸条	"删除成功"
出错了	一张皱巴巴的报错单（HTML）	Tomcat 错误页面
前端的心情：我永远不知道这次打开包裹，里面是一摞书、一张纸条、还是一张报错单。我得每次都猜。
目标：所有包裹用"同一个纸箱"
无论里面装的是书、纸条还是空气，纸箱上的标签格式永远一样：
JSON
{
  "code": 200,        // 物流状态：200=成功送达，500=出问题
  "msg": "success",   // 快递员留言：具体发生了什么
  "data": ...         // 真正的货物：书、空、或其他
}
前端只需要学一套拆包裹流程：
先看 code：200 就拆 data，500 就看 msg
永远不用猜里面是什么格式
📐 Result<T>
Result<T> 是什么？
java
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;     // ← 这个 T 是"占位符"
}
用餐厅比喻：
Result 是外卖盒
<T> 是盒子里装什么菜的标记
code 和 msg 是盒子上的标签（固定不变）
data 是盒子里装的菜（随 T 变化）
T 的三种变身
表格
调用方式	T 变成什么	盒子标签	实际用途
Result<List<Book>>	List<Book>	Result<List<Book>>	装一摞书（查询全部）
Result<Book>	Book	Result<Book>	装一本书（查询单个）
Result<Void>	Void	Result<Void>	什么都不装（删除）
为什么 Void 不是 void？
表格
void	Void
身份	Java 关键字	java.lang 包里的一个类
能用在泛型里吗	❌ 不能	✅ 能
实际值	没有值	永远是 null
泛型 <T> 只接受类，不接受关键字。所以"什么都不返回"要写成 Result<Void>。
🏭 静态工厂方法：为什么 Result.success() 要加 static？
假设不写 static（错误示范）
java
// 你想得到一个成功的结果
Result<List<Book>> r = new Result<>();  // 先造一个空盒子
r = r.success(bookList);                // 用对象去调用方法
问题：
success() 明明和具体哪个盒子没关系，它只是一个"造盒子的工厂"
你却必须先 new 一个空盒子才能用，这个空盒子本身毫无意义
就像你去工厂买手机，工厂说"你先买一部手机，我再给你生产一部"
写成 static 之后（正确做法）
java
Result.success(bookList)   // 直接通过"工厂名"生产
好处：
不用先造空对象：类名直接调用，语义清晰
强制规范：你无法在外部 new Result<>() 然后乱填 code=999，只能通过我提供的工厂，保证"成功=200，失败=500"
命名即意图：Result.success(...) 读出来就是"我要一个成功的结果"
口诀：static 表示这个方法属于"Result 这个概念"，不属于"某一个 Result 对象"。
🌊 解剖一次"显示全部"的新数据流
后端打包（Controller 发货）
java
@GetMapping
public Result<List<Book>> getAll() {
    return Result.success(bookService.findAll());
}
发生了什么？
bookService.findAll() → 厨师把一摞书交给服务员
Result.success(...) → 服务员把书装进标准纸箱，贴上标签 code=200, msg=success
发给前端 → 快递员把纸箱送出
后端发出的 JSON：
JSON
{
  "code": 200,
  "msg": "success",
  "data": [
    {"id": 1, "name": "西游记", "displayOrder": 1},
    {"id": 2, "name": "红楼梦", "displayOrder": 2}
  ]
}
前端拆包（顾客收快递）
JavaScript
fetch(API)
    .then(res => res.json())   // 把纸箱拆开，得到整个 JSON 对象
    .then(res => {             // res = {code: 200, msg: "success", data: [...]}
        if (res.code === 200) {
            allBooks = res.data;  // ← 真正的书在 res.data 里！
        } else {
            showMsg(res.msg, true);  // ← 出错看 res.msg
        }
    });
关键变化：以前 data 直接是数组，现在 data 是信封，data.data 才是数组。
 */