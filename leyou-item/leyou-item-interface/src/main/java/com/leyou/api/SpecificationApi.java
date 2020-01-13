package com.leyou.api;

import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/spec")
public interface SpecificationApi {

    /**
     * @Description: 根据不同的条件查询规格参数 required：参数是否必传，默认为 true，可以设置为非必传 false；
     * @Param gid
     * @Param cid
     * @Param generic
     * @Param searching
     * @DATE 2019/12/9 13:41
     * @return {@link List<SpecParam>}
     */
    @GetMapping("/params")
    public List<SpecParam> queryParam(
            @RequestParam(value = "gid",required = false) Long gid ,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    );
    /**
     * @Description: 根据分类id查询规格参数组合规格参数
     * @Param cid
     * @DATE 2019/12/13 19:00
     * @return {@link List< SpecGroup>}
     */
    @GetMapping("/group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParams(@PathVariable("cid")Long cid);



}



