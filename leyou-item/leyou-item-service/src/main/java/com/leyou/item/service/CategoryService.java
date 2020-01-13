package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /*
    *根据父节点id查询子节点
    *@param pid
    *@return
    * */

    public List<Category> queryCategoryByPid(Long pid){
        Category t = new Category();
        t.setParentId(pid);
        return this.categoryMapper.select(t);
    }

    /**修改brand
     * 1. 回显品牌的分类信息
     * 2.修改 brand
     * @param bid
     * @return
     */

    public List<Category> queryCategoryByBid(Integer bid) {
        List<Integer> cids = this.categoryMapper.selectCidByBid(bid);
        //根据category_id查找category

        return this.categoryMapper.selectCategoryById(cids);
    }

     public List<String> queryNameByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
       /* List<String> names = categories.stream().map(category -> {
            return (String) category.getName();
        }).collect(Collectors.toList());
        return names;*/
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }

}
