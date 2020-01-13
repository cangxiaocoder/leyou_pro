package com.leyou.interceptor;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    /*JDK 1.2的版本中就提供java.lang.ThreadLocal，ThreadLocal为解决多线程程序的并发问题提供了一种新的思路。
    使用这个工具类可以很简洁地编写出优美的多线程程序，ThreadLocal并不是一个Thread，而是Thread的局部变量。*/
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Autowired
    private JwtProperties jwtProperties;
    /**
     * @Description: 前置拦截器
     * @Param request
     * @Param response
     * @Param handler
     * @DATE 2019/12/21 19:25
     * @return {@link boolean}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());

        //解析token
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        //将userinfo放入线程局部变量
        THREAD_LOCAL.set(userInfo);
        return true;

    }

    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程池局部变量，因为使用的是Tomcat线程池，线程不会结束，也就不会线程局部变量
        THREAD_LOCAL.remove();
    }
}
