package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//泛型，<实体类，id类型>
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {


}
