package com.cesela.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.cesela.gmall.bean.PmsProductSaleAttr;
import com.cesela.gmall.bean.PmsSkuInfo;
import com.cesela.gmall.bean.PmsSkuSaleAttrValue;
import com.cesela.gmall.service.SkuService;
import com.cesela.gmall.service.SpuService;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date:2020/7/22 17:03
 */
@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    //thymeleaf测试程序
    @RequestMapping(value = "/index")
    public String index(Model model) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据" + i);
        }
        model.addAttribute("data", "hello,world");
        model.addAttribute("list", list);
        model.addAttribute("check", "1");
        return "index";
    }


    @RequestMapping(value = "{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map, HttpServletRequest request) {

        String remoteAddr = request.getRemoteAddr();

        // request.getHeader("");// nginx负载均衡
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,remoteAddr);

        //sku对象
        map.put("skuInfo", pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), pmsSkuInfo.getId());
        map.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        //查询当前 sku 的 spu 的其他 sku 的集合的 hash 表
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";// "239|245"
            }
            skuSaleAttrHash.put(k,v);
        }

        // 将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }


}
