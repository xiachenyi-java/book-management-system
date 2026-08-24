package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1516:06
 */
public class A9第十课 {
}
/*
📝 第 10 课：日志规范——系统的"行车记录仪"
核心对比
表格
System.out.println	SLF4J + @Slf4j
输出	仅控制台	控制台 + 文件
级别控制	无	DEBUG < INFO < WARN < ERROR
性能	强制字符串拼接	占位符 {} 延迟拼接
生产环境	重启即丢失	文件持久保存，自动切割
关键语法
java
@Slf4j
@Service
public class BookService {
    public Book add(String name) {
        log.info("添加图书: name={}", name);     // 正常流程
        log.debug("查询图书: id={}", id);        // 调试信息
        log.error("系统异常", e);                // 错误 + 堆栈
        // ...
    }
}
配置
properties
logging.level.com.example2.demo2=INFO
logging.file.name=logs/book-manager.log

 */