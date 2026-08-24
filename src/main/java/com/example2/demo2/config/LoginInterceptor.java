package com.example2.demo2.config;

import com.example2.demo2.common.JwtUtil;
import com.example2.demo2.common.RequireAdmin;
import com.example2.demo2.common.UserContext;
import com.example2.demo2.dto.UserContextDTO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 夏辰义
 * 2026/8/2117:59
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
             HttpServletResponse response,Object handle) throws Exception{
        // 如果是预检请求（OPTIONS），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        //判断token存在
        if (token == null || token.isEmpty()){
            writeUnauthorized(response);
            return false;
        }
        //去掉Bearer
        if (token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        try {
            jwtUtil.parseToken(token);
        }catch (Exception e){
            writeUnauthorized(response);
            return false;
        }
        String blacklistKey = "blacklist:token:" + token;
        Boolean isBlacklisted = stringRedisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            writeUnauthorized(response);  // 返回 401
            return false;
        }
        Claims claims = jwtUtil.parseToken(token);
        // 设置 ThreadLocal
        UserContextDTO dto = new UserContextDTO();
        dto.setUsername(jwtUtil.getUsernameFromToken(token));
        dto.setUserId(jwtUtil.getUserIdFromToken(token));
        dto.setRole(claims.get("role",String.class));
        UserContext.setUser(dto);

        //
        if (handle instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handle;
            if (handlerMethod.hasMethodAnnotation(RequireAdmin.class)){
                String role = UserContext.getUser().getRole();
                if (!"ADMIN".equals(role)) {
                    writeForbidden(response, "权限不足，需要管理员身份");
                    return false;
                }
            }
        }
        // 放行
        return true;
    }
    private void writeUnauthorized(HttpServletResponse response)
            throws Exception {
        response.setStatus(401);                                    // HTTP 状态码 401
        response.setContentType("application/json;charset=UTF-8");  // 告诉浏览器这是 JSON
        response.getWriter().write("{\"code\":401,\"msg\":\"未登录或token无效\"}");
        response.getWriter().flush();                               // 立即发送，不要缓冲
    }

    private void writeForbidden(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(403);                                    // HTTP 403
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"" + msg + "\"}");
        response.getWriter().flush();
    }
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserContext.remove();
    }
}
