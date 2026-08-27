package com.example2.demo2.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 夏辰义
 * 2026/8/2614:10
 */
@Entity
@Table(name = "novel")
@Data
public class Novel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;//书名（原来 Book 的 name）

    @Column(length = 2000)
    private String summary; //简介

    private String coverUr1;//封面图地址

    @Column(nullable = false)
    private String category;//类别

    @Column(nullable = false)
    private String status;//状态

    private  Integer totalWords;//总字数

    @Column(nullable = false)
    private LocalDateTime lastUpdateTime;//上次更新时间

    @PrePersist
    protected void onCreate(){
        lastUpdateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = LocalDateTime.now();
    }

    public void setChapters(List<Chapter> chapters) {
    }
}
