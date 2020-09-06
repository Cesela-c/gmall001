package com.cesela.gmall.passport.comtroller;

import com.alibaba.fastjson.JSON;
import com.cesela.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2020/8/28 17:41
 */

public class TestOath2 {

    public static void main(String[] args) {

        getCode();

        //getAccess_token();

        //getUser_info();

    }

     static String getCode(){
        // 获得授权码
        // 1280172475
        // http://passport.gmall.com:8085/vlogin
         String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=1280172475&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");
         //                                     https://api.weibo.com/oauth2/authorize?client_id=1280172475&response_type=code&redirect_uri=http://passport.gmall.com:8085/vloginn
        System.out.println(s1);

        // code:d81954d10d88eabc790661a217b4e052
        //String s2 = "http://passport.gmall.com:8085/vlogin?code=a032cfe43255cf915c3e0795315f6339";
        return null;
    }

    static String getAccess_token(){
        // code:d81954d10d88eabc790661a217b4e052
        // af0f4fc357b62acfa75f8346233185d4
        String s3 = "https://api.weibo.com/oauth2/access_token?client_id=1280172475&client_secret=af0f4fc357b62acfa75f8346233185d4&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=5a7a4d6b7bdbb1b03dd494a7887fe42d";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","1280172475");
        paramMap.put("client_secret","af0f4fc357b62acfa75f8346233185d4");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","5a7a4d6b7bdbb1b03dd494a7887fe42d");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进

        String access_token = HttpclientUtil.doPost(s3, paramMap);
        Map<String, String> access_map = JSON.parseObject(access_token, Map.class);
        System.out.println(access_map.get("access_token"));
        System.out.println(access_map.get("uid"));
        //用access_token查询用户信息  2.00Rm3wmFJ2Td5Bb5e57e74f9nHZUKB
        return access_map.get("access_token");
    }

    // uid: 5303838873
    static Map<String, String> getUser_info() {
        //用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00Rm3wmFJ2Td5Bb5e57e74f9nHZUKB&uid=5303838873";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String, String> user_map = JSON.parseObject(user_json, Map.class);
        System.out.println(user_map.get("1"));
        return user_map;
    }
}
