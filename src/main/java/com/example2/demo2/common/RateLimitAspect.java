package com.example2.demo2.common;

import com.example2.demo2.common.annotation.RateLimit;
import com.example2.demo2.common.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 夏辰义
 * 2026/8/24 18:44
 */
@Aspect
//声明这是一个 AOP 切面类
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    //@Around：环绕通知，是 AOP 中最强大的通知类型。
    // 它能在目标方法执行前和执行后都插入逻辑。
    // "@annotation(rateLimit)"：切点表达式，匹配所有"带有 @RateLimit 注解的方法"
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        //注解上面数据,链接点，封装数据
        String key = rateLimit.key();
        int limit = rateLimit.limit();
        int window = rateLimit.window();

        String ip = getClientIp();
        String redisKey = "rate_limit:" + key + ":" + ip;

        // 修复：统一使用 stringRedisTemplate
        //原子自增INCR
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            stringRedisTemplate.expire(redisKey, window, TimeUnit.SECONDS);
        }
        if (count != null && count > limit) {
            throw new RateLimitException("请求过于频繁，请稍后再试");
        }

        //放行目标方法
        return point.proceed();
    }

     //获取客户端真实 IP
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果 X-Forwarded-For 有多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip != null ? ip : "unknown";
    }
}