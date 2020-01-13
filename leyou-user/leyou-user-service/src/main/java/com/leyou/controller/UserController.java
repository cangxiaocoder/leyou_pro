package com.leyou.controller;

import com.leyou.service.UserService;
import com.leyou.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.soap.SOAPBinding;
import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @Description: 校验身份是否可以注册
     * @Param data
     * @Param type
     * @DATE 2019/12/17 20:58
     * @return {@link ResponseEntity< Boolean>}
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean flag = this.userService.checkUser(data,type);
        if(flag==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(flag);
    }

    /**
     * @Description: 发送验证码
     * @Param phone
     * @DATE 2019/12/17 20:57
     * @return {@link ResponseEntity< Void>}
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendCheckCode(@RequestParam("phone")String phone){
        this.userService.sendCheckCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @Description: 用户注册，
     * @Param user
     * @Param code
     * @DATE 2019/12/18 11:19
     * @return {@link ResponseEntity< Void>}
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code")String code){

        this.userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @Description: 查询功能，根据参数中的用户名和密码查询指定用户,对外提供接口
     * @Param username
     * @Param password
     * @DATE 2019/12/18 12:25
     * @return {@link ResponseEntity< User>}
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,@RequestParam("password")String password){
      User user = this.userService.queryUser(username,password);
      if(user==null){
          return ResponseEntity.badRequest().build();
      }
        return ResponseEntity.ok(user);
    }

}
