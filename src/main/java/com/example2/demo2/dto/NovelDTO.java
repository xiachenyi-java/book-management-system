package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2617:59
 */
@Data
public class NovelDTO {
    @NotBlank(message = "书名不能为空")
    private String title;

    @NotBlank(message = "简介不能为空")
    private String summary;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String coverUrl;
}
