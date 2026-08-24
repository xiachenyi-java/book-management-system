package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1513:59
 */
public class A7第八版思路 {
}
/*
🔍 用"图书馆查书"理解后端搜索
你原来的"病"：让前台堆满书
JavaScript
fetch(API)  // ← 管理员把图书馆 10 万本书搬到前台
    .then(res => res.data.filter(book => {  // ← 顾客自己一本本翻
        return book.name.includes("东野");
    }));
问题：10 万本书搬到前台，前台堆不下，顾客翻到手酸，90% 的书根本没用。
现在的做法：告诉管理员书名，他直接去书架拿
java
// 后端：管理员直接去书架找
List<Book> findByNameContaining(String keyword);
// 自动生成 SQL：SELECT * FROM book WHERE name LIKE '%东野%'
JavaScript
// 前端：只传一个关键词
fetch(`${API}/search?keyword=东野`)  // ← 只拿回 3 本匹配的
JPA 方法名推导魔法：
findBy → 查询
Name → 按 name 字段
Containing → 包含，自动生成 LIKE %keyword%
📄 用"餐厅点菜"理解分页
你原来的"病"：厨房一次做 100 道菜
JavaScript
// 后端：把 100 道菜全部端上桌
// 前端：顾客只看前 5 道，其他的堆在旁边占地方
allBooks.slice(0, 5)  // 前端 JS 切数组
问题：
厨房累瘫（后端生成 100 条 JSON，内存压力大）
传菜员跑断腿（网络传 100 条数据，流量爆炸）
桌子堆不下（浏览器存 100 条，渲染 100 行 DOM）
现在的做法：每次只上 5 道，吃完再点
java
// 后端：只查当前页的 5 条
Pageable pageable = PageRequest.of(page - 1, size);
return bookRepository.findAllByOrderByDisplayOrderAsc(pageable);
JavaScript
// 前端：点"下一页"，再拿 5 条
fetch(`${API}?page=2&size=5`)
🏗️ Page 对象的解剖
后端返回的 JSON 信封里，data 是一个分页对象：
JSON
{
  "content": [           // ← 当前页的 5 本书（表格数据）
    {"id": 6, "name": "围城", "displayOrder": 6}
  ],
  "totalElements": 100,  // ← 总共 100 本（页码信息："共 X 本"）
  "totalPages": 20,      // ← 总共 20 页（分页按钮数量）
  "number": 1            // ← 当前是第几页（从 0 开始）
}
前端只需要记三个字段：
res.data.content → 给表格
res.data.totalElements → 给页码信息
res.data.totalPages → 给分页按钮
📊 前端分页 vs 后端分页对比
表格
对比项	前端分页（旧）	后端分页（新）
后端查多少数据	全部（100 条或 10 万条）	只查 5 条
网络传输量	巨大	极小
后端内存压力	大	小
浏览器内存压力	巨大	几乎没有
DOM 渲染压力	巨大	几乎没有
翻页速度	快（本地切数组）	稍慢（发 HTTP 请求）
结论：数据量大了，前端分页根本不可用，必须用后端分页。
 */