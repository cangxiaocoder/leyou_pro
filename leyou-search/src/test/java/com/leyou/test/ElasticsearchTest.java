package com.leyou.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.LeyouSearchApplication;
import com.leyou.bo.SpuBo;
import com.leyou.common.pojo.PageResult;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/*
* 导入数据值导入一次
* */
@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchTemplate  elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;
    @Test
    public void createIndex(){
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        int rows = 100;
        do {
            PageResult<SpuBo> result = this.goodsClient.querySpuBoByPages(null, true, page, rows);

            List<SpuBo> items = result.getItems();
            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            goodsRepository.saveAll(goodsList);

            page++;
            //最后一页的记录数小于100，或者为0
            rows = items.size();
        }while (rows==100);

    }


}
