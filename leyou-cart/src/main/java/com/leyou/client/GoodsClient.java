package com.leyou.client;

import com.leyou.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "leyou-item-service")
public interface GoodsClient extends GoodsApi {
}
