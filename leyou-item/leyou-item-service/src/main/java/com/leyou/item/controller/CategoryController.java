package com.leyou.item.controller;

import com.leyou.item.service.CategoryService;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * @Description: 根据父节点id查询子节点
     * @Param pid
     * @DATE 2019/12/9 13:27
     * @return {@link ResponseEntity<List<Category>>}
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(name = "pid",defaultValue = "0")Long pid){
        if(pid==null||pid<0){
            //400 请求参数不合法
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.queryCategoryByPid(pid);
        if(CollectionUtils.isEmpty(categories)){
            //404 资源服务器未找到
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }


    /*
     * 修改brand
     * 1. 回显品牌的分类信息
     * 2.修改 brand
     * */
    @GetMapping("/update/{id}")
    public ResponseEntity<List<Category>> querycategoryByBId(@PathVariable("id")Integer bid){
        List<Category> categories = this.categoryService.queryCategoryByBid(bid);

        if(CollectionUtils.isEmpty(categories)){
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
    @GetMapping
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNameByIds(ids);
        if(CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
