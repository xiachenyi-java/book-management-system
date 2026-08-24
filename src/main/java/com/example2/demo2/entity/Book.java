package com.example2.demo2.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 夏辰义
 * 2026/7/2018:14
 */
@Entity
//告诉 Spring："这是一个数据库实体类"。加了 @Entity，JPA 才会把这个类和数据库表对应起来。
@Table(name = "book")
//指定这个实体对应的数据库表名叫 book。如果不写，默认会用类名 Book 作为表名。
@Data
//@Data 是 Lombok 的"全家桶"注解，它会自动生成：
//getter
//setter
//toString
//equals
//hashCode
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /*
    @Id：声明 id 是主键。
    @GeneratedValue(strategy = GenerationType.IDENTITY)：主键由数据库自动生成（MySQL 的自增 ID）。每次插入新数据，数据库会自动给 id 赋值。
    Integer：主键类型用包装类，因为数据库里的 ID 可能是 null（还没生成时）。
     */
    @Column(nullable = false)
    private String name;
    /*
    @Column(nullable = false)：数据库里 name 字段不允许为空。
    String name：书名，对应数据库的 VARCHAR 类型。
     */
    @Column(nullable = false)
    private Integer displayOrder;

}
