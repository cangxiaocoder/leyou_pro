package com.leyou.service;

import com.leyou.client.GoodsClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.dao.CartDao;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoCartService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private GoodsClient goodsClient;
    /** @Description: 创建MongoDB集合
    * @Param: []
    * @return
    */
    public void createDatabase(){
        this.mongoTemplate.createCollection(Cart.class);
    }

    private Cart setCart(UserInfo userInfo, Cart cart) {
        Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
        cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : sku.getImages().split(",")[0]);
        cart.setOwnSpec(sku.getOwnSpec());
        cart.setPrice(sku.getPrice());
        cart.setTitle(sku.getTitle());
        cart.setUserId(userInfo.getId());
        return cart;
    }

    /**
     * @Description: 新增购物车
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity < Void>}
     */
    public void addCart(Cart cart){

        //获取登录用户的信息,
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        System.out.println("userInfo = " + userInfo);
        //查询用户购物车记录是否存在此商品
        Query query = Query.query(Criteria.where("userId").is(userInfo.getId()).and("skuId").is(cart.getSkuId()));
        Cart mongoCart = this.mongoTemplate.findOne(query,Cart.class);

        if (mongoCart==null){
            //不存在
            this.mongoTemplate.insert(setCart(userInfo,cart));
        }else {
            //已存在
            this.mongoTemplate.updateFirst(query, Update.update("num",cart.getNum()+mongoCart.getNum()),Cart.class);
        }
    }

    /**
     * @Description: 查询购物车中的商品
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    public List<Cart> queryCarts() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        return this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userInfo.getId())), Cart.class);
    }

    /**
     * @Description: 修改购物车中的商品数量
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    public void updateNum(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Query query = Query.query(Criteria.where("userId").is(userInfo.getId()).and("skuId").is(cart.getSkuId()));
        Cart cart1 = this.mongoTemplate.findOne(query, Cart.class);
        assert cart1!=null;
        this.mongoTemplate.updateFirst(query,Update.update("num",cart1.getNum()).set("num",cart.getNum()),Cart.class);

    }

    /**
     * @Description: 删除购物车的一件商品
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    public void deleteCart(Long skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        this.mongoTemplate.findAndRemove(Query.query(Criteria.where("userId").is(userInfo.getId()).and("skuId").is(skuId)),
                Cart.class);
    }

    /**
     * @Description: 登录后合并客户端和服务端的购物车
     * @Param cart
     * @DATE 2019/12/22 13:09
     * @return {@link ResponseEntity< Void>}
     */
    public void mergeCart(List<Cart> carts) {

        //获取用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //获取当前用户MongoDB中的购物车集合
        List<Cart> cartList = this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userInfo.getId())),Cart.class);
        //获取购物车搜易商品
        if (CollectionUtils.isEmpty(cartList)){
            List<Cart> collect = carts.stream().map(cart -> setCart(userInfo, cart)).collect(Collectors.toList());
            this.mongoTemplate.insertAll(collect);
        } else {
            //将MongoDB数据库中没有的放入一个集合中；
            List<Cart> list = new ArrayList<>();
            for (Cart cart : carts) {
                Query query = Query.query(Criteria.where("userId").is(userInfo.getId()).and("skuId").is(cart.getSkuId()));
                if (this.mongoTemplate.exists(query,Cart.class)){
                    Update update = new Update();
                    update.inc("num",cart.getNum());
                    this.mongoTemplate.updateFirst(query,update,Cart.class);
                } else {
                    list.add(setCart(userInfo,cart));
                }
            }
            this.mongoTemplate.insertAll(list);
        }
    }
}
