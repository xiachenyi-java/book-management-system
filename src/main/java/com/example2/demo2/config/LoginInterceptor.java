package com.example2.demo2.config;

import com.example2.demo2.common.JwtUtil;
import com.example2.demo2.common.annotation.RequireAdmin;
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

    //预处理
    @Override
    public boolean preHandle(HttpServletRequest request,
             HttpServletResponse response,Object handle) throws Exception{
        // 如果是预检请求（OPTIONS），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 用 getServletPath() 可以去掉上下文路径（比如 /demo2），直接用相对路径判断
        String path = request.getServletPath();
        String method = request.getMethod();
        //定义游客规则：必须是 GET 请求，并且路径以 /novels 开头
        boolean isGuestEndpoint = "GET".equalsIgnoreCase(method) && path.startsWith("/novels");
        String token = request.getHeader("Authorization");

        //去掉Bearer
        if (token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }


        //是游客的处理
        if (isGuestEndpoint){
            if (token != null && !token.isEmpty()){
                try {
                    Claims claims = jwtUtil.parseToken(token);
                    // 检查黑名单（如果在黑名单里，就不设置用户上下文，直接当游客）
                    Integer userId = Integer.valueOf(claims.getSubject());
                    String hashKey = "blacklist:user:" + userId;
                    Object expireObj = stringRedisTemplate.opsForHash().get(hashKey,token);
                    boolean shouldSetContext = true;  // 默认当他是合法登录用户
                  if (expireObj != null){
                      long expireAt = Long.parseLong(expireObj.toString());
                      if (expireAt >= System.currentTimeMillis()){
                          shouldSetContext = false;
                      }else if (expireAt < System.currentTimeMillis()){
                          stringRedisTemplate.opsForHash().delete(hashKey,token);
                      }
                  }
                  if (shouldSetContext){
                      UserContextDTO dto = new UserContextDTO();
                      dto.setUsername(jwtUtil.getUsernameFromToken(token));
                      dto.setUserId(jwtUtil.getUserIdFromToken(token));
                      dto.setRole(claims.get("role",String.class));
                      UserContext.setUser(dto);
                  }
                } catch (Exception e) {
                    // Token 无效，什么也不做（不打印错误，保持日志干净）
                }
            }
        }

        //不是游客的处理
        if (!isGuestEndpoint){

            //判断token存在
            if (token == null || token.isEmpty()){
                writeUnauthorized(response);
                return false;
            }
            //解析token
            Claims claims;
            try {
                claims = jwtUtil.parseToken(token);
            }catch (Exception e){
                writeUnauthorized(response);
                return false;
            }

            //查黑名单
            Integer userId = Integer.valueOf(claims.getSubject());
            String hashKey = "blacklist:user:" + userId;
            Object expireObj = stringRedisTemplate.opsForHash().get(hashKey,token);
           if (expireObj != null){
               long expireAt = Long.parseLong(expireObj.toString());
               if (expireAt > System.currentTimeMillis()){
                   //在黑名单里拦截
                   writeUnauthorized(response);
                   return false;
               }else {
                   //过期了，删除放行
                   stringRedisTemplate.opsForHash().delete(hashKey,token);
               }
           }

            // 设置 ThreadLocal
            UserContextDTO dto = new UserContextDTO();
            dto.setUsername(jwtUtil.getUsernameFromToken(token));
            dto.setUserId(jwtUtil.getUserIdFromToken(token));
            dto.setRole(claims.get("role",String.class));
            UserContext.setUser(dto);

            //判断身份
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
