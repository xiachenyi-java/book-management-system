package com.example2.demo2.service;

import com.example2.demo2.common.JwtUtil;
import com.example2.demo2.common.UserContext;
import com.example2.demo2.common.exception.BusinessException;
import com.example2.demo2.dto.LoginDTO;
import com.example2.demo2.dto.UserContextDTO;
import com.example2.demo2.dto.UserRegisterDTO;
import com.example2.demo2.entity.User;
import com.example2.demo2.repository.UserRepository;
import com.example2.demo2.vo.LoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 夏辰义
 * 2026/8/2015:36
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate stringRedisTemplate;

    //注册
    public User register(UserRegisterDTO dto){
        if (userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new BusinessException("用户名已存在");
        }
        log.info("注册用户: username={}",dto.getUsername());

        //创建实体
        User user = new User();
        user.setRole("USER");
        user.setUsername(dto.getUsername());

        //加密密码
        String hashed =bCryptPasswordEncoder.encode(dto.getPassword());
        user.setPasswordHash(hashed);

        return userRepository.save(user);
    }

    //登陆
    public LoginVO login(LoginDTO dto){
        //账号是否存在,然后拿到user
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        //密码是否正确
        if (!(bCryptPasswordEncoder.matches(dto.getPassword(), user.getPasswordHash()))){

            throw new BusinessException("用户名或密码错误");
        }
        //生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(),user.getRole());
        //双令牌
        String refreshToken = UUID.randomUUID().toString();
        //
        stringRedisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,//key改成token本身
                String.valueOf(user.getId()),//value改成用户id
                7, TimeUnit.DAYS
        );
        //清除敏感信息
        user.setPasswordHash(null);
        // 创建 LoginVO，塞入 token 和 userInfo
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserInfo(user);

        return loginVO;
    }

    //获取当前用户
    public User getCurrentUser(){
        //1.获取ThreadLocal用户
        UserContextDTO user = UserContext.getUser();
        if (user.getUserId() == null){
            throw new BusinessException("用户未登录");
        }
        long userId = user.getUserId();

        //2. 查 Redis 缓存
        String key ="user:info:" + userId;
        String userJson = stringRedisTemplate.opsForValue().get(key);
        if (userJson != null){
            // 缓存命中，JSON 反序列化后直接返回
            try {
                return objectMapper.readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                log.error("Redis 缓存用户数据解析失败, userId={}, json={}",userId,userJson,e);
                stringRedisTemplate.delete(key);
            }
        }

        // 3.缓存未命中，查数据库
        User dbUser = userRepository.findById(user.getUserId()).orElseThrow(()
                -> new BusinessException("用户不存在"));
        // 4. 写入 Redis（设置过期时间，防止永久驻留）
        try {
            String json = objectMapper.writeValueAsString(dbUser);
            stringRedisTemplate.opsForValue().set(
                    key,json,// Jackson
                    30,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            log.error("用户数据序列化失败，写入Redis缓存异常, userId={}", userId, e);
        }
        return dbUser;
    }

    //刷新令牌
    public LoginVO refresh(String refreshToken) {
        // ========== 1.直接查，不需要遍历，不要keys==========
        String userIdStr = stringRedisTemplate.opsForValue().get("refresh_token:" + refreshToken);
        if (userIdStr == null) {
            throw new BusinessException("刷新令牌已过期或不存在");
        }

        Integer userId = Integer.valueOf(userIdStr);
        String key = "refresh:" + userId;

        // ========== 2. 删除旧的 Refresh Token ==========
        stringRedisTemplate.delete("refresh_token:" + refreshToken);

        // ========== 3. 查用户信息 ==========
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // ========== 4. 生成新的 Access Token ==========
        String newAccessToken = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        // ========== 5. 生成新的 Refresh Token ==========
        String newRefreshToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                "refresh_token:" + newRefreshToken,
                String.valueOf(user.getId()),
                7, TimeUnit.DAYS
        );

        // ========== 6. 组装返回 ==========
        LoginVO vo = new LoginVO();
        vo.setToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);  // 返回新的
        user.setPasswordHash(null);
        vo.setUserInfo(user);

        return vo;
    }
}
