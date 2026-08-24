package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1123:50
 */
public class A1第二版思路 {
}
/*
用"餐厅"理解你的四层架构
想象你的系统是一家餐厅：
层级	餐厅角色	职责	你的代码
前端页面	顾客	看菜单、点菜、吃菜	index.html
Controller	服务员	接待顾客、记菜单、端菜上桌、处理投诉	BookController
Service	厨师	决定怎么做菜、计算调料比例、把控口味	BookService
Repository	仓库管理员	去冰箱取食材、清点库存、记录进货	BookRepository
Entity	食材清单	定义" chicken 长什么样、重量多少"	Book
数据库	冰箱/仓库	真正存放食材的地方	MySQL
关键规则：顾客（前端）不能直接冲进厨房（Service）喊"给我拿块肉"！必须叫服务员（Controller）
。厨师（Service）不能直接去冰箱（数据库）翻东西，必须叫仓库管理员（Repository）。
前端点击"添加" → 书名传到后端，数据在你的代码里经历了什么？
第 1 站：Controller（服务员接单）
第 2 站：Service（厨师做菜）
第 3 站：Repository（仓库管理员存冰箱）
第 4 站：数据库（冰箱）
✅ Entity（Book）—— 合格
你定义了数据库表的结构。目前它是贫血模型（只有字段和 getter/setter，没有业务方法），这对初学者完全够用。
一个小问题：你的包名叫 Entity（大写 E），Java 包名规范是全小写。应该叫 entity。现在能跑，但进公司会被老同事吐槽。
✅ Repository —— 合格
你掌握了两种查数据的方式：
继承自带：save()、findById() 是 JpaRepository 送的
方法名推导：findAllByOrderByDisplayOrderAsc() Spring 会自动生成 SQL
手写 JPQL：@Query("SELECT MAX...") 应对复杂需求
这三种武器够你用半年。
✅ Service —— 基本合格，有一个 Bug
你的业务逻辑都搬进来了：算序号、判断存在性、重排。很好
 */