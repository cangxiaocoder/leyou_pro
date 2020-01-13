package com.leyou.service;

import com.leyou.client.GoodsClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.pojo.Cart;
import com.leyou.pojo.Sku;
import com.leyou.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;



    private static final String KEY_PREFIX = "user:cart:";

    public void addCart(Cart cart) {

        //获取登录用户的信息,
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询用户购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断购物车中是否已存在次商品
        if(hashOps.hasKey(cart.getSkuId().toString())){
            //存在，更新cart.num
            String cartJson = Objects.requireNonNull(hashOps.get(key)).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            assert cart != null;
            cart.setNum(cart.getNum()+num);
        } else {
            //不存在，新增一条记录
            setCart(userInfo, cart);
        }
        hashOps.put(key, Objects.requireNonNull(JsonUtils.serialize(cart)));

    }


      public List<Cart> queryCarts() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX+userInfo.getId().toString();
        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(key)){
          return null;
        }
        //获取用户购物车记录
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        //获取map中所以cart值集合
        List<Object> cartsJson = hashOps.values();

        if(CollectionUtils.isEmpty(cartsJson)){
          return null;
        }
        //将List<Object>转化为List<cart>
        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(),Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String cartJson = Objects.requireNonNull(hashOps.get(cart.getSkuId().toString())).toString();
        Integer num = cart.getNum();
        cart = Objects.requireNonNull(JsonUtils.parse(cartJson, Cart.class));
        cart.setNum(num);
        hashOps.put(cart.getSkuId().toString(), Objects.requireNonNull(JsonUtils.serialize(cart)));

    }

    public void deleteCart(Long skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId.toString());
    }

    public void mergeCart(List<Cart> carts) {

        //获取用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX+userInfo.getId());

        //获取map中所以cart值集合
        List<Object> cartsJson = hashOps.values();

        //获取购物车搜易商品
        if (CollectionUtils.isEmpty(cartsJson)){
            for (Cart cart : carts) {
                setCart(userInfo,cart);
                hashOps.put(cart.getSkuId().toString(), Objects.requireNonNull(JsonUtils.serialize(cart)));
            }
        } else {
            for (Cart cart : carts) {
                if(hashOps.hasKey(cart.getSkuId().toString())){
                    //存在，更新cart.num
                    Integer num = cart.getNum();
                    String cartJson = Objects.requireNonNull(hashOps.get(cart.getSkuId().toString())).toString();
                    cart = JsonUtils.parse(cartJson, Cart.class);
                    assert cart != null;
                    cart.setNum(cart.getNum()+num);
                } else {
                    //不存在，新增一条记录
                    setCart(userInfo, cart);
                }
                hashOps.put(cart.getSkuId().toString(), Objects.requireNonNull(JsonUtils.serialize(cart)));
            }
        }

    }

    private void setCart(UserInfo userInfo, Cart cart) {
        Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
        cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : sku.getImages().split(",")[0]);
        cart.setOwnSpec(sku.getOwnSpec());
        cart.setPrice(sku.getPrice());
        cart.setTitle(sku.getTitle());
        cart.setUserId(userInfo.getId());
    }
}
