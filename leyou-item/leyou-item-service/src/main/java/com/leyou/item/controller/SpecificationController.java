package com.leyou.item.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.leyou.item.service.SpecificationService;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;

@Controller
@RequestMapping("/spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specService;

    /*
     * 根据id删除SpecGroup信息
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("id") Long id) {
        this.specService.deleteGroupById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

//  =====================================================================================

    /*
     * 根据id删除SpecParam信息
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteParamById(@PathVariable("id") Long id) {
        this.specService.deleteParamById(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
     * 根据分类id查询参数组
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Integer cid) {
        List<SpecGroup> groups = this.specService.queryGroupByCid(cid);

        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }

    /**
     * @Description: 根据不同的条件查询规格参数 required：参数是否必传，默认为 true，可以设置为非必传 false；
     * @Param gid
     * @Param cid
     * @Param generic
     * @Param searching
     * @DATE 2019/12/9 13:41
     * @return {@link ResponseEntity<List<SpecParam>>}
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParam(
            @RequestParam(value = "gid",required = false) Long gid ,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ) {
        List<SpecParam> params = this.specService.queryParam(gid,cid,generic,searching);

        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(params);
    }

    /*
     * 增加规格参数组SpecGroup信息
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroup(SpecGroup group) {
        System.out.println(group);
        this.specService.saveGroup(group);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 增加规格参数SpecParam信息
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveParam(SpecParam param) {
        System.out.println(param);
        this.specService.saveParam(param);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 修改SpecGroup信息
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateGroup(SpecGroup group) {
        System.out.println("------------------------------------");
        System.out.println(group);
        this.specService.updateGroup(group);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 修改SpecParam信息
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateParam(SpecParam param) {
        this.specService.updateParam(param);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @Description: 根据分类id查询规格参数组合规格参数
     * @Param cid
     * @DATE 2019/12/13 19:00
     * @return {@link ResponseEntity< List< SpecGroup>>}
     */
    @GetMapping("/group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParams(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = this.specService.queryGroupsWithParams(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}



