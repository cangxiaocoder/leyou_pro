package com.leyou.goods.client;

import com.leyou.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("leyou-item-service")
public interface GoodsClient extends GoodsApi {

}
