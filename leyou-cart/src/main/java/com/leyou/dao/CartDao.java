package com.leyou.dao;

import com.leyou.pojo.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartDao extends MongoRepository<Cart,String> {
}
