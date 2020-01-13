package com.leyou.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //设置前缀，便于区分保存在Redis中的是什么
    private static final String KEY_PREFIX = "user:check:";

    /**
     * @Description: 校验身份是否可以注册
     * @Param data
     * @Param type
     * @DATE 2019/12/17 20:58
     * @return {@link Boolean}
     */
    public Boolean checkUser(String data, Integer type) {

        User user = new User();
        if(type==1){
            user.setUsername(data);
        }else if(type==2){
            user.setPhone(data);
        }else {
            return null;
        }
       return this.userMapper.selectCount(user) == 0;
    }

    /**
     * @Description: 发送验证码
     * @Param phone
     * @DATE 2019/12/17 20:57
     * @return {@link  Void}
     */
    public void sendCheckCode(String phone) {
        if(StringUtils.isBlank(phone)){
            return;
        }
        //生产验证码
        String code = NumberUtils.generateCode(6);

        //发送消息到rabbitmq
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","sms.check.code",msg);

        //把验证码保存在Redis中
        redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);

    }

    /**
     * @Description: 发送验证码
     * @Param phone
     * @DATE 2019/12/17 20:57
     * @return {@link ResponseEntity< Void>}
     */
    public void register(User user, String code) {
        //1取出Redis中的验证码
        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //2比较验证码
        if(!StringUtils.equals(redisCode,code)){
            return;
        }
        //3密码加盐,加密
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //4新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);
        //减少资源浪费，删除Redis中的验证码
        this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    public void addUser(User user) {
        //3密码加盐,加密
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //4新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);
        //减少资源浪费，删除Redis中的验证码
        this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    /**
     * @Description: 查询功能，根据参数中的用户名和密码查询指定用户
     * @Param username
     * @Param password
     * @DATE 2019/12/18 12:25
     * @return {@link User>}
     */
    public User queryUser(String username, String password) {
        //根据用户名查询用户
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断用户是否存在
        if(user==null){
            return null;
        }
        //为用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, user.getSalt());
        //与数据库密码比较
        if(StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;

    }
}
