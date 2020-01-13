package com.leyou.api;

import com.leyou.bo.SpuBo;
import com.leyou.common.pojo.PageResult;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*对外接口*/
public interface GoodsApi {
    /* @Description: 根据spuId查询Spudetail
     * @Param: [spuId]
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail queryDetailBySpuId(@PathVariable("spuId")Long spuId);

    /*
     *@Description 根据分页查询spu
     *@Param [key, saleable, page, rows]
     *@Return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuBoByPages(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /* @Description: 根据spuId查询Sku集合
     * @Param: [spuId]
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id")Long spuId);
    /**
     * @Description: 根据spuid 查询spu
     * @Param id
     * @DATE 2019/12/13 18:49
     * @return {@link Spu}
     */
    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id")Long id);

    /**
     * @Description: 根据skuid 查询sku,对外提供接口
     * @Param id
     * @DATE 2019/12/13 18:49
     * @return {@link Sku}
     */
    @GetMapping("sku/{id}")
    public Sku querySkuBySkuId(@PathVariable("id")Long id);

}
