package com.leyou.api;

import com.leyou.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/test")
public interface TestApi {

    @GetMapping("/brand")
    public Brand findBrand(@RequestParam("id")Long id);

}
