package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.pojo.*;
import com.leyou.search.client.*;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private TestClient testClient;

   @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * @Description: 将spu转成goods
     * @Param spu
     * @DATE 2019/12/9 15:34
     * @return {@link Goods}
     */
    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //获取分类名称
        List<String> names = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //获取品牌名称
        //Brand brand = brandClient.queryBrandById(spu.getBrandId());
        Brand brand = testClient.findBrand(spu.getBrandId());
        //查询skus
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        //初始化价格集合，收集所有的sku价格
        List<Long> prices = new ArrayList<>();
        //收集sku必要字段信息
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("price",sku.getPrice());
            //获取sku中的图片，搜索时只需要展现一张图片，多张图片一“,”分割
            map.put("image",StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            map.put("title",sku.getTitle());
            skuMapList.add(map);
        });

        //根据spu中的cid3查询出所以的搜索规格参数
        List<SpecParam> specParams = specificationClient.queryParam(null, spu.getCid3(), null, true);

        //根据spuId查询spuDetail
        SpuDetail spuDetail = goodsClient.queryDetailBySpuId(spu.getId());
        //把通用规格参数值进行反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {});
        //把特殊规格参数值进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});
        Map<String, Object> specs = new HashMap<>();

        specParams.forEach(param ->{
            //判断规格参数类型，是否是通用参数
            if(param.getGeneric()){
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断参数值是否属数字类型，数字类型的值返回一个取之区间
                if(param.getNumeric()){
                   value = chooseSegment(value, param);
                }
                specs.put(param.getName(),value);
            }else {
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要标题、分类名称和品牌名称，添加空格，防止将两个不同的字段（标题和分类名称）合在一起进行分词
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ") +" "+brand.getName());
        //获取spu下所有sku的价格
        goods.setPrice(prices);
        //获取spu下所有sku，并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数{name:value}
        goods.setSpecs(specs);
        return goods;
    }

    //判断参数值是否属数字类型，过滤参数中有一类比较特殊，就是数值区间：
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            //判断区间是否是最后一个区间，如果是最后一个区间则没有最大值
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult search(SearchRequest request) {
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        //添加条件
        builder.withQuery(basicQuery);
        //添加分页,分页页码从0开始
        builder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加结果集过滤
        builder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //添加分类和品牌的聚合
        String categoryAggName="categories";
        String brandAggName = "brands";
        builder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        builder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //执行查询
        AggregatedPage<Goods> goodsPage =(AggregatedPage<Goods>) goodsRepository.search(builder.build());

        //获取聚合斌解析
        List<Map<String,Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //判断是否只有一个分类，只有一个分类是才做聚合，
        List<Map<String,Object>> specs =null;
        if(!CollectionUtils.isEmpty(categories) && categories.size()==1){
            //对规格参数聚合
        specs = getParamAggResult((Long)categories.get(0).get("id"),basicQuery);
        }

        return new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),categories,brands,specs);
    }

    /**
     * @Description: 构建bool查询
     * @Param request
     * @DATE 2019/12/12 19:00
     * @return {@link BoolQueryBuilder}
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));

        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if(StringUtils.equals(key,"品牌")){
                key="brandId";
            }else if(StringUtils.equals(key,"分类")){
                key = "cid3";
            }else {
                key = "specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));

        }
        return boolQueryBuilder;
    }


    /**
     * @Description: 根据查询条件聚合分类
     * @Param aggregation
     * @DATE 2019/12/12 12:55
     * @return {@link List< Map< String, Object>>}
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取bucket的集合转化成List<Map<String, Object>>集合
        return terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            //获取桶分类id
            Long id = bucket.getKeyAsNumber().longValue();
            //根据分类id查询名称
            List<String> names = this.categoryClient.queryNameByIds(Collections.singletonList(id));
            map.put("id",id);
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());
    }
    /**
     * @Description: 根据查询条件聚合品牌
     * @Param aggregation
     * @DATE 2019/12/12 12:54
     * @return {@link List< Brand>}
     */
    /*private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

        this.brandClient.test(76L);
        System.out.println("\"=========\" = " + "=========");
        return terms.getBuckets().stream().map(bucket -> {
           System.out.println(bucket.getKeyAsNumber().longValue());
           Long id = bucket.getKeyAsNumber().longValue();
           return this.brandClient.queryBrandById(id);
       }).collect(Collectors.toList());
    }*/

    private List<Brand> getBrandAggResult(Aggregation aggregation){
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            Long id = bucket.getKeyAsNumber().longValue();
            return this.testClient.findBrand(id);
        }).collect(Collectors.toList());
        //this.testClient.queryBrandById(76L);


    }

    /**
     * @Description: 根据查询条件聚会规格参数
     * @Param cid
     * @Param basicQuery
     * @DATE 2019/12/12 12:55
     * @return {@link List< Map< String, Object>>}
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParam(null,cid,null,true);
        params.forEach(param ->{
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+".keyword"));
        });
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行查询，获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        List<Map<String, Object>> specs = new ArrayList<>();
        //解析聚合结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
                //{k：规格参数名，options:规格参数值}
            Map<String, Object> map= new HashMap<>();
            map.put("k",entry.getKey());

            List<String> options = new ArrayList<>();
            StringTerms terms = (StringTerms)entry.getValue();
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);
            specs.add(map);
        }

        return specs;
    }

    /*监听增删改，重新建立Elasticsearch*/

    public void create(Long id) throws IOException {
        Spu spu = goodsClient.querySpuById(id);
        Goods goods = buildGoods(spu);

        goodsRepository.save(goods);
    }

    public void delete(Long id) {
        goodsRepository.deleteById(id);
    }
}
