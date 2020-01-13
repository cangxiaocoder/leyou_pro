package com.leyou.item.mapper;

import com.leyou.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {
    @Select("select category_id from tb_category_brand where brand_id=#{bid}")
    List<Integer> selectCidByBid(Integer bid);
    @Select("<script> select * from tb_category where id in " +
            "<foreach item='item' index='index' collection='cids' open='(' separator=',' close=')' >#{item}</foreach>" +
            " </script>")
    List<Category> selectCategoryById(@Param("cids") List<Integer> cids);
}
