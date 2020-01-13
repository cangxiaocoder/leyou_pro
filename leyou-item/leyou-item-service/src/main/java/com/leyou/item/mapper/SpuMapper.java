package com.leyou.item.mapper;

import com.leyou.pojo.Spu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SpuMapper extends Mapper<Spu> {
    @Update("update tb_spu set valid=0 where id=#{spuId}")
    void updateValidBySpuId(@Param("spuId") Long spuId);
    @Update("update tb_spu set saleable=#{saleable} where id=#{id}")
    void updateSaleableById(@Param("saleable") Boolean saleable,@Param("id") Long id);
}
