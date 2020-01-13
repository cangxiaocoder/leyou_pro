package com.leyou.item.controller;

import com.leyou.bo.SpuBo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import java.util.List;

@Controller
@RequestMapping("/")
public class  GoodsController {
    @Autowired
    private GoodsService goodsService;


    /*
    *@Description 根据分页查询spu
    *@Param [key, saleable, page, rows]
    *@Return
    */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPages(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> result = this.goodsService.querySpuBoByPages(key,saleable,page,rows);
        if(result==null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /* @Description: 新增商品
    * @Param: [spuBo]
    * @return
    */
    @PostMapping("goods")
    public ResponseEntity<Void>saveGoods(@RequestBody SpuBo spuBo){
        this.goodsService.saveGoods(spuBo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* @Description: 根据spuId查询Spudetail
    * @Param: [spuId]
    * @return
    */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail>queryDetailBySpuId(@PathVariable("spuId")Long spuId){
       SpuDetail spuDetail = this.goodsService.queryDetailBySpuId(spuId);
       if(spuDetail==null){
           return ResponseEntity.notFound().build();
       }
    return ResponseEntity.ok(spuDetail);
    }

    /* @Description: 根据spuId查询Sku集合
     * @Param: [spuId]
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        System.out.println(spuId);
        List<Sku> skus = this.goodsService.querySkuBySpuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
       return ResponseEntity.ok(skus);
    }

    /* @Description: 修改商品信息
    * @Param: [spuBo]
    * @return
    */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        //更新成功响应204
        return ResponseEntity.noContent().build();
    }

    /* @Description: 根据spuId删除商品
    * @Param: [spuId]
    * @return
    */
    @DeleteMapping("/goods/{id}")
    public ResponseEntity<Void> deleteGoodsBySpuId(@PathVariable("id")Long spuId){
        this.goodsService.deleteGoodsBySpuId(spuId);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Description: 商品上下架管理
     * @Param saleable
     * @Param id
     * @DATE 2019/12/16 14:17
     * @return {@link ResponseEntity< Void>}
     */
    @PutMapping("/goods/{saleable}/{id}")
    public ResponseEntity<Void> updateSaleableById(@PathVariable("saleable")Boolean saleable,@PathVariable("id")Long id){
        System.out.println(saleable+"=="+id);
        this.goodsService.updateSaleableById(saleable,id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Description: 根据spuid 查询spu
     * @Param id
     * @DATE 2019/12/13 18:49
     * @return {@link ResponseEntity< Spu>}
     */
    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if(spu==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);

    }

    /**
     * @Description: 根据skuid 查询sku,对外提供接口
     * @Param id
     * @DATE 2019/12/13 18:49
     * @return {@link ResponseEntity< Sku>}
     */
    @GetMapping("sku/{id}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("id")Long id){
        Sku sku = this.goodsService.querySkuBySkuId(id);
        if(sku==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);

    }

}
