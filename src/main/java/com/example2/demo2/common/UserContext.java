package com.example2.demo2.common;

import com.example2.demo2.dto.UserContextDTO;

/**
 * 夏辰义
 * 2026/8/2117:53
 */
public class UserContext {

    //局部线程变量
    private static final ThreadLocal<UserContextDTO> userHolder = new ThreadLocal<>();

    public static void setUser(UserContextDTO user) {
        userHolder.set(user);
    }
    public static UserContextDTO getUser() {
        return userHolder.get();
    }
    public static void remove() {
        userHolder.remove();
    }
}
