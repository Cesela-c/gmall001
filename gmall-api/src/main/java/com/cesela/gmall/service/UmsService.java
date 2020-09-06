package com.cesela.gmall.service;


import com.cesela.gmall.bean.UmsMember;
import com.cesela.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @Date:2020/6/27 9:51
 */

public interface UmsService {

    /**
     * 根据用户ID查询用户信息学
     * @return
     */
    List<UmsMember> getAllUser();

    /**
     * 查询所有用户地址
     * @return
     */
    List<UmsMemberReceiveAddress> getReceiveAddressBtMemberId(String memberId);


    //登录
    UmsMember login(UmsMember umsMember);


    void addUserToken(String token, String memberId);

    public UmsMember addOauthUser(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);

    UmsMember getOauthUser(UmsMember umsMemberCheck);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);
}
