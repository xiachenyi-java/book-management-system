package com.example2.demo2.common;

import com.example2.demo2.common.exception.BusinessException;
import com.example2.demo2.common.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    //限流异常异常
    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)  // 这个注解会让 HTTP 状态码变成 429
    public Result<Void> handleRateLimit(RateLimitException e) {
        log.warn("触发限流: {}", e.getMessage());
        return Result.error(e.getStatus(), e.getMessage());
    }

    //用户异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e){
        log.warn("用户问题: {}",e.getMessage());
        return Result.error(e.getMessage());
    }
    //上传文件异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleFileSizeException(MethodArgumentNotValidException e){
        log.warn("传输文件过大: {}",e.getMessage());
        return Result.error("上传失败：文件大小不能超过 10MB");
    }

    //;所有异常最后的底裤
    @ExceptionHandler(Exception.class)
    public Result<Void> Exception(Exception e){
        log.warn("最后的异常处理器:",e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
