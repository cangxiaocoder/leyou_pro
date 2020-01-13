package com.leyou;

import com.leyou.item.service.GoodsService;
import com.leyou.pojo.Spu;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = LeyouItemApplication.class)
@RunWith(SpringRunner.class)
public class Test {

    @Autowired
    private GoodsService goodsService;

    @org.junit.Test
    public void find(){
        Spu spu = this.goodsService.querySpuById(2L);
        System.out.println("spu = " + spu);
    }
}
