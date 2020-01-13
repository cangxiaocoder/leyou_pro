package com.leyou.search.client;

import com.leyou.api.GoodsApi;
import com.leyou.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("leyou-item-service")
public interface GoodsClient extends GoodsApi {

}
