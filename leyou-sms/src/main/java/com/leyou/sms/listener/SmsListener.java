package com.leyou.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties smsProperties;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.QUEUE",durable = "true"),
            exchange =@Exchange(value = "LEYOU.SMS.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"sms.check.code"}
    ))
    public void sendMsg(Map<String,String> msg){

        if(CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if(StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code)){
            try {
                this.smsUtils.sendSms(phone,code,this.smsProperties.getSignName(),this.smsProperties.getVerifyCodeTemplate());
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
    }

}
