package com.cesela.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cesela.gmall.bean.PmsSkuInfo;
import com.cesela.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @Date:2020/7/21 15:36
 */
@Controller
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    @RequestMapping(value = "/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {

        //将spuId封装给productId
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        //处理默认图片
        String skuDefoultImage = pmsSkuInfo.getSkuDefaultImg();
        if (StringUtils.isBlank(skuDefoultImage)) {
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }

        skuService.saveSkuInfo(pmsSkuInfo);
        return "seccess";

    }
}
