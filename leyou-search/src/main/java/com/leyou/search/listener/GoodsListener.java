package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value =@Queue( value = "LEYOU.SEARCH.CREATE.QUEUE",durable = "true"),
            exchange=@Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void create(Long id) throws IOException {

        if(id==null){
            return;
        }
        this.searchService.create(id);
    }
    @RabbitListener(bindings = @QueueBinding(
            value =@Queue( value = "LEYOU.SEARCH.DELETE.QUEUE",durable = "true"),
            exchange=@Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) throws IOException {

        if(id==null){
            return;
        }
        this.searchService.delete(id);
    }

}
