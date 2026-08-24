package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/1321:45
 */
@Data
public class BookDTO {
    /*
    核心作用
DTO 是一个纯粹的数据容器，用于在不同层、不同服务或不同进程之间传输数据。
它只包含字段（属性）和 getter/setter 方法，不包含业务逻辑。
     */
    @NotBlank(message = "书名不能为空")
    private String name;
    /*
        @NotBlank 是什么意思？
不能是 null
不能是空字符串 ""
不能全是空格 "   "
     */
}
