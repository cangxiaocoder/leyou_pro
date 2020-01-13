package com.leyou.item.mapper;

import com.leyou.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface BrandMapper extends Mapper<Brand> , SelectByIdListMapper<Brand,Integer> {
    @Select("insert into tb_category_brand (category_id,brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(Integer cid,Integer bid);
    @Update("update tb_category_brand set category_id=#{cid} where brand_id=#{bid}")
    void updateCategoryAndBrand(@Param("cid") Integer cid, @Param("bid") Integer bid);
    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    void deleteCategoryAndBrand(@Param("bid") Integer bid);
    @Select("select brand_id from tb_category_brand where category_id=#{cid}")
    List<Integer> selectIdsByBid(Long cid);
    @Select("select * from tb_brand a inner join tb_category_brand b on a.id = b.brand_id where b.category_id=#{cid}")
    List<Brand> selectBrandByCid(Long cid);
}
