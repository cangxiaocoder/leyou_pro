package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * @Description: 用户登录，生成用户登录token令牌，保存在cookie中
     * @Param username
     * @Param password
     * @DATE 2019/12/20 13:10
     * @return {@link String}
     */
    public String login(String username, String password) {

        User user = userClient.queryUser(username, password);

        if(user==null){
            return null;
        }

        //生产jwt类型的token
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo,this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
