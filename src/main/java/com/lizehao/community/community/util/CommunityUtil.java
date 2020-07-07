package com.lizehao.community.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    //上传文件和头像还有生成salt需要用到
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");

    }

    //MD5加密
    //单纯用MD5加密是不安全的，因为固定的字符串生成的加密字符串是固定的
    //所以需要用到user表里的salt，密码+salt然后再通过MD5加密
    public static String md5(String key){
        //空串，空格、null都判断为空，lang3包里的工具
        if(StringUtils.isBlank(key)){
            return null;

        }
        //把传入的字符串加密成十六进制的字符串返回（SpringBoot自带）
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
