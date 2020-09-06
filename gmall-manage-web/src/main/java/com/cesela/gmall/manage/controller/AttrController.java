package com.cesela.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cesela.gmall.bean.PmsBaseAttrInfo;
import com.cesela.gmall.bean.PmsBaseAttrValue;
import com.cesela.gmall.bean.PmsBaseSaleAttr;
import com.cesela.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Date:2020/7/5 22:08
 */
@Controller
@CrossOrigin
public class AttrController {

    @Reference
    private AttrService attrService;

    @RequestMapping(value = "/attrInfoList")
    public @ResponseBody List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }

    @RequestMapping(value = "/saveAttrInfo")
    public @ResponseBody String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String seccess = attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "seccess";
    }

    @RequestMapping(value = "/getAttrValueList")
    public @ResponseBody List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }

    @RequestMapping(value = "/baseSaleAttrList")
    public @ResponseBody List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrService.getBaseSaleAttr();
        return pmsBaseSaleAttrs;
    }

}
