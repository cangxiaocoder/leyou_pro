package com.leyou.goods.client;

import com.leyou.api.CategoryAPi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("leyou-item-service")
public interface CategoryClient extends CategoryAPi {
}
