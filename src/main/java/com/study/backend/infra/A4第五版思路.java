package com.study.backend.infra;

/**
 * 夏辰义
 * 2026/8/1216:13
 */
public class A4第五版思路 {
}
/*
✂️ 用"自动代笔秘书"理解 Lombok
你现在的"病"：写代码像写八股文
以前写 Book.java，真正重要的只有 3 行：
java
private Integer id;
private String name;
private Integer displayOrder;
但为了这 3 行，你被迫写了 18 行 getter/setter，占满屏幕，真正重要的字段反而被淹没了。
还有 BookService 和 BookController，每个类都要写：
java
public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
}
这句话没有任何业务逻辑，只是"把右边的值塞给左边"，但你每个类都要写一遍。
Lombok 是什么？
它是一个站在你背后的代笔秘书。你口述需求，它自动帮你写那些无聊的样板代码。
你看到的代码（清爽）：
java
@Data
public class Book {
    private Integer id;
    private String name;
    private Integer displayOrder;
}
编译后的代码（完整）：
java
public class Book {
    private Integer id;
    private String name;
    private Integer displayOrder;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public String toString() { ... }
    public boolean equals(Object o) { ... }
    public int hashCode() { ... }
}
关键：你的 .java 文件里看不到这些方法，但编译后的 .class 文件里全都有。Spring 运行时读的是 .class，所以一切正常。
🏗️ Lombok 的两个核心注解
1. @Data —— 全家桶
贴在类头上，自动生成：
getter（所有字段）
setter（所有非 final 字段）
toString()
equals()
hashCode()
适用场景：实体类（Entity）、DTO（后面会学）
2. @RequiredArgsConstructor —— 构造器工厂
贴在类头上，自动为所有 final 字段生成构造器。
java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;  // ← 因为有 final，所以生成构造器
}
等价于：
java
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
为什么只给 final 字段生成？
因为 final 表示"这个字段赋值后不能改"。Lombok 的设计哲学是：只有必须初始化的字段，才需要放进构造器。如果字段不是 final，说明它可以在对象创建后再赋值，不需要强制通过构造器注入。
 */