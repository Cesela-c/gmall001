package com.cesela.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.cesela.gmall.bean.Constants;
import com.cesela.gmall.bean.UmsMember;
import com.cesela.gmall.bean.UmsMemberReceiveAddress;
import com.cesela.gmall.service.UmsService;
import com.cesela.gmall.user.mapper.UmsMapper;
import com.cesela.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.cesela.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Date:2020/6/27 9:53
 */

@Service
public class UmsServiceImpl implements UmsService {

    @Autowired
    private UmsMapper umsMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {

        /*List<UmsMember> umsMemberList = umsMapper.selectAllUser();*/
        List<UmsMember> umsMemberList = umsMapper.selectAll(); //通用mapper方法

        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressBtMemberId(String memberId) {

        /*Example e = new Example(UmsMemberReceiveAddress.class);
        e.createCriteria().andEqualTo("memberId", memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.selectByExample(e);*/

        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);

        return umsMemberReceiveAddressList;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if(jedis!=null){
                String umsMemberStr = jedis.get(Constants.user + umsMember.getPassword() + umsMember.getUsername() + Constants.info);
                if (StringUtils.isNotBlank(umsMemberStr)) {
                    // 密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            // 链接redis失败，开启数据库
            UmsMember umsMemberFromDb =loginFromDb(umsMember);
            if(umsMemberFromDb!=null){
                jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        }finally {
            jedis.close();
        }
    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers = umsMapper.select(umsMember);

        if(umsMembers!=null){
            return umsMembers.get(0);
        }
        return null;
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            jedis.setex(Constants.user + memberId + Constants.token, 60 * 60 * 2, token);
        } finally {
            jedis.close();
        }
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        UmsMember umsMember = umsMapper.selectOne(umsCheck);
        return umsMember;
    }

    @Override
    public UmsMember getOauthUser(UmsMember umsMemberCheck) {
        UmsMember umsMember = umsMapper.selectOne(umsMemberCheck);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        umsMapper.insertSelective(umsMember);
        return umsMember;
    }
}
