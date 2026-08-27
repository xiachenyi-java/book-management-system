package com.example2.demo2.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 夏辰义
 * 2026/8/2418:39
 */
@Target(ElementType.METHOD)
//这个注释只能标记在方法上
@Retention(RetentionPolicy.RUNTIME)
//运行时还保留在jvm里，可以通过反射读取
public @interface RateLimit {

    String key();

    int limit() default 5;

    int window() default 60;
}
