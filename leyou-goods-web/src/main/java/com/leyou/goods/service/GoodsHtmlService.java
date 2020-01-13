package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;

/*页面静态化*/
@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private GoodsWebService goodsWebService;

    public void createHtml(Long spuId){
        //初始化上下文
        Context context = new Context();
        context.setVariables(this.goodsWebService.loadData(spuId));
        File file = new File("F:\\Manager\\Nginx\\nginx-1.16.1\\html\\item\\" + spuId + ".html");
        try (PrintWriter printWriter = new PrintWriter(file)) {
            templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deleHtml(Long spuId) {
        File file = new File("F:\\Manager\\Nginx\\nginx-1.16.1\\html\\item\\" + spuId + ".html");
        file.deleteOnExit();

    }

    /**
     * 新建线程处理页面静态化，防止线程阻塞
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId)); //lambda表达式
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }


}
