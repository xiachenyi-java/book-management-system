package com.example2.demo2.controller;

import com.example2.demo2.entity.User;
import com.example2.demo2.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 夏辰义
 * 2026/8/2312:53
 */
@SpringBootTest
@AutoConfigureMockMvc   // 自动配置 MockMvc
@Transactional //事务
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;//模拟 HTTP 客户端

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("123456"));
        user.setRole("USER");
        userRepository.save(user);
    }

    @Test
    void testRegisterSuccess() throws Exception{
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"newuser\",\"password\":\"123456\"}"))
                        .andExpect(status().isOk())//HTTP 200
                .andExpect(jsonPath("$.code").value(200));
        // 你的 Result.code 是 200
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        // 再注册一次 setUp 里已经有的 "testuser"
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))   // 你的 Result 错误码是 500
                .andExpect(jsonPath("$.msg").value("用户名已存在"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())  // 返回里有 token
                .andExpect(jsonPath("$.data.userInfo.username").value("testuser"));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    void testAccessBooksWithoutToken() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());  // ← 401，被 LoginInterceptor 拦住
    }

    @Test
    void testAccessBooksWithToken() throws Exception {
        // 1. 先登录拿到 token
        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"123456\"}"))
                .andReturn();

        // 2. 从 JSON 里提取 token（用 JsonPath）
        String response = result.getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.data.token");

        // 3. 带 token 访问图书接口
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUserCannotDeleteBook() throws Exception {
        // 1. 普通用户登录拿 token
        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"123456\"}"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.data.token");

        // 2. 普通用户调 DELETE（需要 ADMIN 权限）
        mockMvc.perform(delete("/books/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());  // ← 403，权限不足
    }
}
