package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.utils.CookieUtils;
import com.leyou.pojo.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * @Description: 用户登录，生成用户登录token令牌，保存在cookie中
     * @Param username
     * @Param password
     * @Param request
     * @Param response
     * @DATE 2019/12/20 13:05
     * @return {@link ResponseEntity< Void>}
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request,
            HttpServletResponse response){
        String token = this.authService.login(username,password);
        if (StringUtils.isBlank(token)){
            //状态码401，身份未认证
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println(username+"==>"+password);
        //cookieMaxAge:单位是秒 cookie生效的最大秒数,所以需要乘以60
        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 每次操作都会发送这个请求，30分钟
     * @Description: 从token中获取用户名，显示在前台页面
     * @Param token
     * @DATE 2019/12/20 16:59
     * @return {@link ResponseEntity<UserInfo>}
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify (
            @CookieValue("LY_TOKEN")String token,
            HttpServletRequest request,
            HttpServletResponse response){

        try {
            //通过jwt工具的公钥解析jwt获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());

            //刷新jwt中的有效时间,单位是分
             token = JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
            //刷新cookie中的有效时间,单位是秒
            CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
