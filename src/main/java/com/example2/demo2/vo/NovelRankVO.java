package com.example2.demo2.vo;

import lombok.Data;

/**
 * 夏辰义
 * 2026/9/814:14
 */
@Data
public class NovelRankVO {

    private Integer NovelId;// 小说ID

    private String Title;// 标题

    private String CoverUrl;// 封面

    private Long Heat;// 热度值（注意用 Long，因为 Double 转过来）
}
