package com.lizehao.community.community.config;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
//配置类（向项目里面添加一些配置的bean实例时用到
public class kaptchaConfig {
    @Bean
    //方法的返回值是bean容器的实例，注入实例的时候就会自动配置
    public Producer kaptchaProducer(){

        //设置验证码信息
        Properties properties = new Properties();
        //长宽
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        //字体大小颜色 0,0,0是黑色（红绿蓝的数值）
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        //验证码信息
        properties.setProperty("kaptcha.textproducer.char.String","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        //把配置给Config,Config再把配置给Kaptcha
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
