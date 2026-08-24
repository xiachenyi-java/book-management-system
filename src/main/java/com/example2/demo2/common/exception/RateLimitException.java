package com.example2.demo2.common.exception;

import lombok.Getter;

/**
 * 夏辰义
 * 2026/8/2418:05
 */
@Getter
public class RateLimitException extends RuntimeException{
    // HTTP 状态码，429 = Too Many Requests
    private final int status = 429;

    public RateLimitException(String message) {
        super(message);
    }
}
