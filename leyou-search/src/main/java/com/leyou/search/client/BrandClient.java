package com.leyou.search.client;

import com.leyou.api.BrandApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/brand")
@FeignClient(value = "leyou-item-service",url = "localhost:8081/brand")
public interface BrandClient extends BrandApi {

    @GetMapping("/test")
    public void test(@RequestParam("id")Long id);
}
