package com.video.filter;


import com.video.pojo.LoginUser;
import com.video.utils.JwtUtils;
import com.video.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: zrq
 * @date: 2022年06月27日 14:03
 */

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //获取token
        String token = request.getHeader("token");
        log.info(token);
        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String username = null;
        try {
            username = JwtUtils.getMemberUsernameByJwtToken(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.isNull(username)) {
            resolver.resolveException(request,response,
                    null,new RuntimeException("token非法"));
        }
        //从redis中获取用户信息
        String redisKey = "login:" + username;
        LoginUser loginUser =  redisCache.getCacheObject(redisKey);
        //刷新token有效期
        redisCache.setCacheObject("login:" + loginUser.getUsername(), loginUser,60*24, TimeUnit.MINUTES);
        if(Objects.isNull(loginUser)){
            resolver.resolveException(request,response,
                    null,new RuntimeException("用户未登录"));
        }

        //存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
