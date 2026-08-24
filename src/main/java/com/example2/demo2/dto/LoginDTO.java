package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2110:38
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
