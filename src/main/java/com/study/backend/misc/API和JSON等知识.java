package com.study.backend.misc;

/**
 * 夏辰义
 * 2026/8/914:48
 */
public class API和JSON等知识 {
}
/*
API应用程序编程接口
作用	说明
封装复杂	隐藏内部实现细节，只暴露简单接口
标准化通信	让不同系统用统一格式交换数据
安全隔离	控制访问权限，保护核心数据
提高效率	开发者无需重复造轮子，直接调用现成功能
JSON 就是一种轻量、易读的数据格式，前后端交互、配置文件、接口返回数据，到处都在用。
bookRepository 是什么？
它是你写的这个接口：
表面看	它是一个接口，你自己写的，里面只有方法签名，没有具体代码
实际上	Spring 在程序启动时，自动生成了一个"实现类"（你从来没见过，但它真实存在）
本质上	它是你和数据库之间的"翻译官"。你调用 save()，它帮你翻译成 INSERT 或 UPDATE SQL
Controller（控制器层） ← 你现在的 delete 方法在这里
    ↓ 调用
Service（业务逻辑层）   ← 复杂项目会加这一层
    ↓ 调用
Repository（数据访问层） ← bookRepository 在这里，负责和数据库打交道
    ↓ 生成 SQL
MySQL 数据库
save() 是什么意思？
它是 JpaRepository 自带的方法，作用是：把 Java 对象存进数据库。
它怎么判断是"新增"还是"修改"？
看对象的 id 是否为 null：
情况	save() 做什么	生成的 SQL
id == null	认为是新数据	INSERT INTO book (...) VALUES (...)
id != null	认为是已有数据	UPDATE book SET ... WHERE id = ?
你调用 save(book)
  ↓
Spring 自动生成的实现类接手
  ↓
Hibernate 检查：book.getId() 有值 → 这是更新操作
  ↓
生成 SQL：UPDATE book SET display_order=2, name='水浒传' WHERE id=5
  ↓
通过 JDBC 发送给 MySQL
  ↓
MySQL 执行更新
  ↓
返回给你：更新成功
 */