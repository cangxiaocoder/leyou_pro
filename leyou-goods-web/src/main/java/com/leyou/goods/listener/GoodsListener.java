package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private   GoodsHtmlService goodsHtmlService;

    /**
     * @Description: 新增修改商品都创建新的静态页面，修改会覆盖以前的静态也没,durable持久化
     * @Param id
     * @DATE 2019/12/16 14:45
     * @return
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.CREATE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void create(Long id){

        if(id==null){
            return;
        }
        goodsHtmlService.createHtml(id);

    }
    /**
     * @Description: 删除商品会删除静态页面
     * @Param id
     * @DATE 2019/12/16 14:45
     * @return
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.DELETE.QUEUE",declare = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id){

        if(id==null){
            return;
        }
        goodsHtmlService.deleHtml(id);

    }

}
