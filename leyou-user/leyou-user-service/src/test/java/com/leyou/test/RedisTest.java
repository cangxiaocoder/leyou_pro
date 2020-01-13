package com.leyou.test;

import com.leyou.LeyouUserApplication;
import com.leyou.service.UserService;
import com.leyou.user.pojo.User;
import com.sun.jersey.core.header.OutBoundHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouUserApplication.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Test
    public void testRedis() {
        // 存储数据
        this.redisTemplate.opsForValue().set("key1", "value1");
        // 获取数据
        String val = this.redisTemplate.opsForValue().get("key1");
        System.out.println("val = " + val);
    }

    @Test
    public void testRedis2() {
        // 存储数据，并指定剩余生命时间,5小时
        this.redisTemplate.opsForValue().set("name", "zhangsan",
                60, TimeUnit.SECONDS);
    }

    @Test
    public void testHash() {
        BoundHashOperations<String, Object, Object> hashOps =
                this.redisTemplate.boundHashOps("user");
        // 操作hash数据
        hashOps.put("name", "jack");
        hashOps.put("age", "21");
        hashOps.expire(60,TimeUnit.SECONDS);

        // 获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name = " + name);

        // 获取所有数据
        Map<Object, Object> map = hashOps.entries();
        for (Map.Entry<Object, Object> me : map.entrySet()) {
            System.out.println(me.getKey() + " : " + me.getValue());
        }
    }

    @Test
    public void query(){
        User user = this.userService.queryUser("lucy", "123456");
        System.out.println("user = " + user);
    }

    @Test
    public void addUser(){
        User user = new User();

        user.setUsername("lucy");
        user.setPassword("123456");
        user.setPhone("15545453213");
        System.out.println(new Date());
        this.userService.addUser(user);
    }
}
