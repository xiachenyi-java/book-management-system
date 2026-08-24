package com.example2.demo2.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 夏辰义
 * 2026/8/1213:38
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e){
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    //校验参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", msg);
        return Result.error(msg);
    }

    //所有异常最后的底裤
    @ExceptionHandler(Exception.class)
    public Result<Void> Exception(Exception e){
        log.warn("最后的异常处理器:",e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
