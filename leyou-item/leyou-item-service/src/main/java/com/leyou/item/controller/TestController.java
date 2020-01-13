package com.leyou.item.controller;

import com.leyou.item.service.BrandService;
import com.leyou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BrandService brandService;
    /**
     * @Description: 根据品牌id查询品牌名称
     * @Param id
     * @DATE 2019/12/9 13:39
     * @return {@link ResponseEntity<  Brand >}
     */
    @GetMapping("/brand")
    public ResponseEntity<Brand> findBrand(@RequestParam("id")Long id){
        Brand brand = this.brandService.queryBrandById(id);
        if (brand==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brand);

    }
}
