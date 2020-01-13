package com.leyou.item.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.pojo.Brand;
import com.leyou.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String  sortBy, Boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria =example.createCriteria();
        //根据key模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //添加分页
        PageHelper.startPage(page,rows);
        //添加排序
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }
    /*
    * 注解事务，保证两表同时增加或修改
    * */
    @Transactional
    public void saveBrand(Brand brand, List<Integer> cids) {
        System.out.println(brand.getId());
        //新增brand
        int i = this.brandMapper.insertSelective(brand);
        System.out.println(brand.getId()+"+++++++++++++++++++++++==");
        //新增中间表
        for (Integer cid :cids) {
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        }
    }
    @Transactional
    public void updateBrandById(Brand brand, List<Integer> cids) {

        this.brandMapper.updateByPrimaryKey(brand);
        for (Integer cid :cids) {
            this.brandMapper.updateCategoryAndBrand(cid,brand.getId());
        }


    }
    @Transactional
    public Integer deleteBrandById(Integer bid) {
        int i = this.brandMapper.deleteByPrimaryKey(bid);
        this.brandMapper.deleteCategoryAndBrand(bid);
        return i;
    }
    /* @Description: 根据cid查询此商品分类的所以品牌
     * @Param: [cid]
     * @return
     * 单表查询
     */
    /*public List<Brand> queryBrandByCid(Long cid) {
        List<Integer> ids = this.brandMapper.selectIdsByBid(cid);
        return this.brandMapper.selectByIdList(ids);
    }*/
    /* @Description: 根据cid查询此商品分类的所以品牌
     * @Param: [cid]
     * @return
     * 多表查询
     */
    public List<Brand> queryBrandByCid(Long cid) {
        return this.brandMapper.selectBrandByCid(cid);
    }

    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);

    }
    public String test(){
        String msg = "测试**********************";
        return msg;
    }
}
