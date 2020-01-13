package com.leyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/*
* cors 解决跨域问题
* cors 配置
* */
@Configuration
public class LeyouCrosConf {
    @Bean
    public CorsFilter corsFilter(){

        //初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        //允许跨域的域名， 如果要携带cookie 不能写成“*” *：所以域名都可以跨域访问
        configuration.addAllowedOrigin("http://manager.leyou.com");
        //允许携带cookie
        configuration.setAllowCredentials(true);
        //允许请求方法 *:所以
        configuration.addAllowedMethod("*");
        //允许携带任何消息头
        configuration.addAllowedHeader("*");

        //初始化cors配置源对象
        //添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);

        //返回CorsFilter实例 参数：cors配置源对象
        return new CorsFilter(configurationSource);
    }
}
