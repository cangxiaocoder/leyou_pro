package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.bo.SpuBo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPages(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //上下架条件
        criteria.andEqualTo("saleable",saleable);
        //是否处于删除状态
        criteria.andEqualTo("valid",1);
        //分页条件
        PageHelper.startPage(page,rows);
        //执行查询，返回Spu
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //将返回的Spu转成SpuBo
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            List<String> names = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));
            return spuBo;
        }).collect(Collectors.toList());
        //返回PageResult<SpuBo>
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }

    //抽取相同的部分
    private void saveSkuAndStock(SpuBo spuBo) {
        Stock stock = new Stock();
        spuBo.getSkus().forEach(sku -> {
            //新增Sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            //新增Stock
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    private void sendMsg(String type ,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
    /* @Description: 新增商品,Transactional提交事务
     * @Param: [spuBo]
     * @return
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //新增Spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());

        this.spuMapper.insertSelective(spuBo);
        //新增SpuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        this.saveSkuAndStock(spuBo);
        sendMsg("insert",spuBo.getId());
    }



    /* @Description: 根据spuId查询Spudetail
     * @Param: [spuId]
     * @return
     */
    public SpuDetail queryDetailBySpuId(Long spuId) {

        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }
    /* @Description: 根据spuId查询Sku集合
     * @Param: [spuId]
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(sku1 -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku1.getId());
            if(stock==null){
                sku1.setStock(0);
            }else {
                sku1.setStock(stock.getStock());
            }
        });
        return skus;
    }
    /* @Description: 修改商品信息
     *修改Spu对Sku来说可能新增，修改或删除
     * 所以直接删除sku旧的信息，全部当做新增
     *删除：先删除字表在删除主表
     * 新增，修改：先操作主表在操作子表
     * @Param: [spuBo]
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //根据skuId删除Stock
        Sku record = new Sku();
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        //根据spuId删除Sku
       Sku sku = new Sku();
       sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);
        this.saveSkuAndStock(spuBo);
        //更新Spu和SpuDetail
        //创建时间不能修改
        spuBo.setCreateTime(null);
        //是否删除不能修改，有单独的删除业务
        spuBo.setValid(null);
        //是否上下架不能修改，有单独的上下架业务
        spuBo.setSaleable(null);
        //添加修改时间
        spuBo.setLastUpdateTime(new Date());
        //有选择的更新
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMsg("update",spuBo.getId());
    }

    /* @Description: 逻辑删除，修改valid字段为0，
    * @Param: [spuId]
    * @return
    */
    public void deleteGoodsBySpuId(Long spuId) {
        this.spuMapper.updateValidBySpuId(spuId);
        sendMsg("delete",spuId);
    }

    /**
     * @Description: 上架下架管理
     * @Param saleable
     * @Param id
     * @DATE 2019/12/16 14:16
     * @return
     */
    public void updateSaleableById(Boolean saleable, Long id) {
        saleable= !saleable;
        this.spuMapper.updateSaleableById(saleable,id);
    }
    /**
     * @Description: 根据spuid 查询spu
     * @Param id
     * @DATE 2019/12/13 18:49
     * @return {@link ResponseEntity < Spu>}
     */
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    /**
     * @Description: 根据skuid 查询sku
     * @Param id
     * @DATE 2019/12/21 21:13
     * @return {@link Sku}
     */
    public Sku querySkuBySkuId(Long id) {

        return this.skuMapper.selectByPrimaryKey(id);
    }
}
