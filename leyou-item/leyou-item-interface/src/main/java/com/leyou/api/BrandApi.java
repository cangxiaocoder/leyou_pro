package com.leyou.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/brand")
public interface BrandApi {
    /**
     * @Description: 根据品牌id查询品牌名称
     * @Param id
     * @DATE 2019/12/9 13:39
     * @return {@link ResponseEntity< Brand>}
     */
    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);



}
