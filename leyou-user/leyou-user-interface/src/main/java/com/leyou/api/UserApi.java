package com.leyou.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    /**
     * @Description: 查询功能，根据参数中的用户名和密码查询指定用户,对外提供接口
     * @Param username
     * @Param password
     * @DATE 2019/12/18 12:25
     * @return {@link User}
     */
    @GetMapping("query")
    public User queryUser(@RequestParam("username")String username, @RequestParam("password")String password);
}
