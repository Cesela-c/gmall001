package com.cesela.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cesela.gmall.bean.PmsBaseCatalog1;
import com.cesela.gmall.bean.PmsBaseCatalog2;
import com.cesela.gmall.bean.PmsBaseCatalog3;
import com.cesela.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Date:2020/7/4 19:03
 */
@Controller
@CrossOrigin
public class CatalogController {

    @Reference
    private CatalogService catalogService;

    @RequestMapping(value = "/getCatalog1")
    public @ResponseBody List<PmsBaseCatalog1> pmsBaseCatalog1s(){
        List<PmsBaseCatalog1> catalog1s = catalogService.getCatalog1();
        return catalog1s;
    }

    @RequestMapping(value = "/getCatalog2")
    public @ResponseBody List<PmsBaseCatalog2> pmsBaseCatalog1s(Integer catalog1Id){
        List<PmsBaseCatalog2> catalog2s = catalogService.getCatalog2(catalog1Id);
        return catalog2s;
    }

    @RequestMapping(value = "/getCatalog3")
    public @ResponseBody List<PmsBaseCatalog3> pmsBaseCatalog3s(Long catalog2Id) {
        List<PmsBaseCatalog3> catalog3s = catalogService.getCatalog3(catalog2Id);
        return catalog3s;
    }
}
