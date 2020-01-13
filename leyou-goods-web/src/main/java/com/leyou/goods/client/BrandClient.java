package com.leyou.goods.client;

import com.leyou.api.BrandApi;
import com.leyou.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/brand")
@FeignClient(value = "leyou-item-service",url = "localhost:8081/brand")
public interface BrandClient extends BrandApi {

    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id")Long id);
}
