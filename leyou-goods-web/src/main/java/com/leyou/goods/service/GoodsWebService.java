package com.leyou.goods.service;

import com.leyou.goods.client.*;
import com.leyou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsWebService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private TestClient testClient;

    public Map<String,Object> loadData(Long spuId){
        Map<String,Object> map = new HashMap<>();
        //根据spuid查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);
        //根据spuid查询spuDetail
        SpuDetail spuDetail = this.goodsClient.queryDetailBySpuId(spuId);
        //根据spu查询category
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNameByIds(cids);
        List<Map<String,Object>>  categories = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {

            Map<String,Object> category = new HashMap<>();
            category.put("id",cids.get(i));
            category.put("name",names.get(i));
            categories.add(category);
        }
        //根据spu查询brand
        Brand brand = this.testClient.findBrand(spu.getBrandId());
        //根据spuId查询skus
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);
        //根据spu查询规格参数组合规格参数
        List<SpecGroup> groups = this.specificationClient.queryGroupsWithParams(spu.getCid3());
        //查询特殊规格参数
        List<SpecParam> params = this.specificationClient.queryParam(null, spu.getCid3(), false, null);
        Map<Long,String> paramMap = new HashMap<>();
        params.forEach(param ->{
            paramMap.put(param.getId(),param.getName());
        });

        map.put("spu",spu);
        map.put("spuDetail",spuDetail);
        map.put("categories",categories);
        map.put("brand",brand);
        map.put("skus",skus);
        map.put("groups",groups);
        map.put("paramMap",paramMap);

        return map;
    }
}
