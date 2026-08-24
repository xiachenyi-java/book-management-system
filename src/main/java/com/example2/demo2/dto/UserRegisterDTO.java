package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2017:04
 */
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 20,min = 3,message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
}
