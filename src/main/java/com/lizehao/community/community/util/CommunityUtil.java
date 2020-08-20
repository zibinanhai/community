package com.lizehao.community.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
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


    /**
     * 把传入数据转换成json数据
     * @param code 编号
     * @param msg 提示
     * @param map 业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if(map != null) {
            for(String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();

    }

    //重载
    public static String getJSONString (int code, String msg) {
        return getJSONString(code, msg, null);
    }

    //重载
    public static String getJSONString (int code) {

        return getJSONString(code, null, null);

    }


}