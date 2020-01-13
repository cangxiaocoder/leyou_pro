package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "specService")
//Specification 规格，说明书
public class SpecificationService {
    @Autowired
    private SpecParamMapper paramMapper;

    @Autowired
    private SpecGroupMapper groupMapper;
    /*
     * 根据分类id查询参数组
     * */
    public List<SpecGroup> queryGroupByCid(long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return this.groupMapper.select(record);
    }
    /*
     * 修改SpecGroup信息
     * */
    public void updateGroup(SpecGroup group) {
        this.groupMapper.updateByPrimaryKey(group);
    }
    /*
     * 增加SpecGroup信息
     * */
    public void saveGroup(SpecGroup group) {
        this.groupMapper.insert(group);
    }

    /*
     * 根据id删除SpecGroup信息
     * */
    public void deleteGroupById(Long id) {
        this.groupMapper.deleteByPrimaryKey(id);
    }
//==========================================================================

    /*
     * 根据分类id查询规格参数
     * */
    public List<SpecParam> queryParam(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return  this.paramMapper.select(record);
    }


    public void updateParam(SpecParam param) {
        this.paramMapper.updateByPrimaryKey(param);
    }

    public void saveParam(SpecParam param) {
        this.paramMapper.insertSelective(param);
    }

    public void deleteParamById(Long id) {
        this.paramMapper.deleteByPrimaryKey(id);
    }

    /* @Description: 根据商品分类id查询规格参数
     * @Param: [cid]
     * @return
     */
    public List<SpecParam> queryParamsByCid(Long cid) {
        SpecParam param = new SpecParam();
        param.setCid(cid);
        return this.paramMapper.select(param);

    }

    /**
     * @Description: 根据分类id查询规格参数组合规格参数
     * @Param cid
     * @DATE 2019/12/13 19:00
     * @return {@link List< SpecGroup>}
     */
    public List<SpecGroup> queryGroupsWithParams(Long cid) {
        List<SpecGroup> groups = this.queryGroupByCid(cid);
        groups.forEach(group ->{
            List<SpecParam> params = this.queryParam(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
