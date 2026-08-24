package com.example2.demo2.vo;

import com.example2.demo2.entity.User;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/2116:06
 */
@Data
public class LoginVO {
    private String token;//jwt令牌

    private User userInfo;//用户信息
}
