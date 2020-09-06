package com.cesela.gmall.user.controller;
import com.cesela.gmall.bean.UmsMember;
import com.cesela.gmall.bean.UmsMemberReceiveAddress;
import com.cesela.gmall.service.UmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Date:2020/6/27 9:48
 */
@Controller
public class UmsController {

    @Autowired
    private UmsService umsService;

    @RequestMapping(value = "/getAllUser")
    public @ResponseBody
    List<UmsMember> getAllUser(){
        List<UmsMember> umsMemberList = umsService.getAllUser();
        return umsMemberList;
    }

    @RequestMapping(value = "/getAllUserAddress")
    public @ResponseBody List<UmsMemberReceiveAddress> getReceiveAddressBtMemberId(Long memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsService.getReceiveAddressBtMemberId(memberId);
        return umsMemberReceiveAddressList;
    }

}
