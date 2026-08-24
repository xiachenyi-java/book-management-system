package com.example2.demo2.controller;

import com.example2.demo2.common.Result;
import com.example2.demo2.common.UserContext;
import com.example2.demo2.dto.LoginDTO;
import com.example2.demo2.dto.UserRegisterDTO;
import com.example2.demo2.entity.User;
import com.example2.demo2.service.UserService;
import com.example2.demo2.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * 夏辰义
 * 2026/8/2018:14
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理")
@Slf4j
public class UserController {
    private final UserService userService;

    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Valid UserRegisterDTO dto){
        return Result.success(userService.register(dto));
    }

    @Operation(summary = "用户登陆")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto){

        return Result.success(userService.login(dto));
    }

    @Operation(
            summary = "用户登出",
            security = @SecurityRequirement(name = "BearerAuth")  // 告诉 Swagger 这个接口需要 Bearer token
    )
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        log.info("用户登出: userId={}", UserContext.getUser().getUserId());
        String key = "blacklist:token:" +token;
        stringRedisTemplate.opsForValue().set(key,"logout",120, TimeUnit.MINUTES);
        return Result.success();
    }
}
