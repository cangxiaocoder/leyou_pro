package com.leyou.controller;

import com.leyou.common.utils.JsonUtils;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.pojo.Cart;
import com.leyou.pojo.UserInfo;
import com.leyou.service.CartService;
import com.leyou.service.MongoCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MongoCartService mongoCartService;

    /**
     * @Description: 新增购物车
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    /*@PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        System.out.println(cart);

        this.cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        System.out.println(cart);

        this.mongoCartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @Description: 查询购物车中的商品
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    /*@GetMapping
    public ResponseEntity<List<Cart>> queryCarts(){

        List<Cart> carts = this.cartService.queryCarts();
        if (CollectionUtils.isEmpty(carts)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carts);
    }*/
    @GetMapping
    public ResponseEntity<List<Cart>> queryCarts(){

        List<Cart> carts = this.mongoCartService.queryCarts();
        if (CollectionUtils.isEmpty(carts)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carts);
    }

    /**
     * @Description: 修改购物车中的商品数量
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
    /*@PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);

        return ResponseEntity.noContent().build();
    }*/
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.mongoCartService.updateNum(cart);

        return ResponseEntity.noContent().build();
    }

    /**
     * @Description: 删除购物车的一件商品
     * @Param cart
     * @DATE 2019/12/21 23:18
     * @return {@link ResponseEntity< Void>}
     */
   /* @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")Long skuId ){

        this.cartService.deleteCart(skuId);

        return ResponseEntity.ok().build();
    }*/
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")Long skuId ){

        this.mongoCartService.deleteCart(skuId);

        return ResponseEntity.ok().build();
    }
    /**
     * @Description: 登录后合并客户端和服务端的购物车
     * @Param cart
     * @DATE 2019/12/22 13:09
     * @return {@link ResponseEntity< Void>}
     */
    /*@PostMapping("merge")
    public ResponseEntity<Void> mergeCart(@RequestBody List<Cart> carts){

        this.cartService.mergeCart(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/
    @PostMapping("merge")
    public ResponseEntity<Void> mergeCart(@RequestBody List<Cart> carts){

        this.mongoCartService.mergeCart(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
