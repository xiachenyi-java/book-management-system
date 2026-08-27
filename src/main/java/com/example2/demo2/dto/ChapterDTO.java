package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2618:00
 */
@Data
public class ChapterDTO {
    @NotBlank(message = "章节标题不能为空")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;
}
