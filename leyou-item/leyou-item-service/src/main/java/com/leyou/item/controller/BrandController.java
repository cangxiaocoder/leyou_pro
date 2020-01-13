package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.BrandService;
import com.leyou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;
    /**
     * @Description:  根据分页信息查询品牌
     * @Param key
     * @Param page
     * @Param rows
     * @Param sortBy
     * @Param desc
     * @DATE 2019/12/9 14:32
     * @return {@link org.springframework.http.ResponseEntity<com.leyou.common.pojo.PageResult<com.leyou.pojo.Brand>>}
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc
    ){
        PageResult<Brand> brands = this.brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if(CollectionUtils.isEmpty(brands.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }
    //新增
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Integer> cids){
        System.out.println(brand+""+cids);
        this.brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Brand> updateBrandById(Brand brand,@RequestParam("cids") List<Integer> cids){
        this.brandService.updateBrandById(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
   /* @Description:
   * @Param: [bid]
   * @return
   */
    @DeleteMapping("/delete/{bid}")
    public ResponseEntity<Integer> deleteBrandById(@PathVariable("bid")Integer bid){
        Integer i = this.brandService.deleteBrandById(bid);
        return ResponseEntity.ok(i);
    }
    /* @Description: 根据cid查询此商品分类的所以品牌
    * @Param: [cid]
    * @return
    */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){
       List<Brand> brands =  this.brandService.queryBrandByCid(cid);
       if(brands==null || CollectionUtils.isEmpty(brands)){
           return ResponseEntity.notFound().build();
       }
        return ResponseEntity.ok(brands);
    }
    /**
     * @Description: 根据品牌id查询品牌名称
     * @Param id
     * @DATE 2019/12/9 13:39
     * @return {@link ResponseEntity< Brand>}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
       Brand brand = this.brandService.queryBrandById(id);
       if (brand==null){
        return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(brand);
    }
}
