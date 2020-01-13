package com.leyou.search.client;

import com.leyou.api.TestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("leyou-item-service")
public interface TestClient extends TestApi {
}
