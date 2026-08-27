package com.example2.demo2.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 夏辰义
 * 2026/8/2618:23
 */
@Data
public class NovelDetailVO {
    private Integer id;
    private String title;
    private String summary;
    private String coverUrl;
    private String category;
    private String status;
    private Integer totalWords;
    private LocalDateTime lastUpdateTime;

    private List<ChapterOutline> chapters;  // 章节目录

    @Data
    public static class ChapterOutline {
        private Integer id;
        private Integer chapterNumber;  // 第几章
        private String title;           // 章节标题
        private Integer wordCount;      // 本章字数
        private LocalDateTime createTime;
    }
}