package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsWebController {

    @Autowired
    private GoodsWebService goodsWebService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;
    /**
     * @Description: 根据spuid查询商品信息，返回商品详情页
     * @Param id
     * @DATE 2019/12/13 19:28
     * @return {@link String}
     */
    @GetMapping("{id}.html")
    public String toItemPage(@PathVariable("id")Long id, Model model){

        Map<String, Object> map = this.goodsWebService.loadData(id);
        model.addAllAttributes(map);
        this.goodsHtmlService.createHtml(id);
        return "item";

    }
}
