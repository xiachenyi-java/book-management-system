package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1717:28
 */
public class A12第13课 {
}
/*
第13课：多环境配置（Profile）
用"餐厅"理解多环境
你的餐厅（项目）要开三家店：
开发店（dev）：你家的厨房，随便折腾，锅糊了没事
测试店（test）：朋友来试吃，规矩一点，但还能容忍上菜慢
生产店（prod）：正式开门迎客，绝对不能出错，顾客看着呢
问题： 三家店的"运营手册"能一样吗？
你家厨房（dev）：煤气灶是 localhost，密码 123456，厨师可以边做边尝（show-sql=true）
正式门店（prod）：中央厨房在阿里云，密码只有店长知道，后厨禁止外人参观（show-sql=false）
所以你需要三套手册，根据今天开哪家店，自动换手册。
第13课核心知识点
表格
概念	餐厅比喻	代码对应
application.properties	所有门店通用的规矩：比如"本店使用 MySQL 数据库"	公共配置（方言、应用名）
application-dev.properties	家厨房专用手册：煤气灶地址、简单密码、随便改装修	开发环境配置（localhost、update、DEBUG）
application-prod.properties	正式门店手册：中央厨房地址、保险柜密码、禁止动装修	生产环境配置（远程地址、validate、WARN）
spring.profiles.active=dev	今天在家厨房试做，拿家厨房的手册	激活开发环境
--spring.profiles.active=prod	店长说："今天正式营业，换正式手册！"	命令行切换环境
${DB_PASSWORD}	保险柜密码不写进手册，每天由店长口头传达	环境变量传密码
ddl-auto=update	家厨房：今天加个灶台，明天换个抽风机，随便改	开发环境自动改表
ddl-auto=validate	正式门店：只检查厨房是否符合消防规范，不动一砖一瓦	生产环境只校验表结构
三个"绝对不能"（保命规则）
保险柜密码（数据库密码）绝对不能写进员工手册（配置文件）然后贴墙上（提交 Git）
正确做法：手册上写"问店长要密码"，店长每天口头传达（${DB_PASSWORD}）
正式门店（生产环境）绝对不能 ddl-auto=update
想象：正式营业时厨师说"我觉得灶台位置不对"，当场拆墙改布局？顾客还在吃饭呢！
正确做法：validate（只检查，不动手），改布局走 Flyway 装修队
手册名字绝对不能写错
正确：application-prod.properties
错误：application_prod.properties（下划线）、prod-application.properties（顺序反了）
顾客（前端）感受不到换手册
无论今天在家厨房试做还是正式营业，顾客看到的菜单（Result<T> 格式）永远一样。换手册是后厨（后端）自己的事，顾客不知道、也不该知道。
一句话总结第13课
一套菜谱（代码），三套厨房手册（配置），根据今天开哪家店（active profile）自动切换。正式门店的保险柜密码，永远只存在于店长嘴里（环境变量），绝不写在纸上（Git）。
 */