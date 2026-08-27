package com.example2.demo2.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 夏辰义
 * 2026/8/2617:41
 */
@Entity
@Table(name = "chapter")
@Data
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer novelId;// 属于哪本小说（外键）

    @Column(nullable = false)
    private Integer chapterNumber;// 第几章：1, 2, 3...

    @Column(nullable = false)
    private String title;// 章节标题

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;// 章节正文（大文本，存3000~10000字）

    @Column(nullable = false)
    private Integer wordCount;// 本章字数

    @Column(nullable = false)
    private LocalDateTime createTime;//创建时间

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}