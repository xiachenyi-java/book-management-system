package com.example2.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2420:11
 */
@Data
public class RefreshTokenDTO {
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
