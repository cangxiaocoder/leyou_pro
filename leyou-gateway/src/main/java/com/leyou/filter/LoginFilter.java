package com.leyou.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.leyou.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.RequestContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        //类型，前置拦截
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {

        List<String> allowPaths = this.filterProperties.getAllowPaths();

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        StringBuffer url = request.getRequestURL();

        for (String allowPath : allowPaths) {
            //如何路径包含白名单，则不拦截，不执行run方法
            if (StringUtils.contains(url,allowPath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        if(StringUtils.isBlank(token)){
            //是否转发请求
            context.setSendZuulResponse(false);
            //响应状态码,身份未确认
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            LOGGER.error("非法访问，未登录，地址：{}", request.getRemoteHost());
        }

        try {
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //是否转发请求
            context.setSendZuulResponse(false);
            //响应状态码,身份未确认
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            LOGGER.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }
        //返回null表示不拦截
        return null;
    }
}
