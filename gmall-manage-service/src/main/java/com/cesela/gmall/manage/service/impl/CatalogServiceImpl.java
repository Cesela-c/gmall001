package com.cesela.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cesela.gmall.bean.PmsBaseCatalog1;
import com.cesela.gmall.bean.PmsBaseCatalog2;
import com.cesela.gmall.bean.PmsBaseCatalog3;
import com.cesela.gmall.manage.service.mapper.PmsBaseCatalog1Mapper;
import com.cesela.gmall.manage.service.mapper.PmsBaseCatalog2Mapper;
import com.cesela.gmall.manage.service.mapper.PmsBaseCatalog3Mapper;
import com.cesela.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Date:2020/7/4 21:38
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;


    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        List<PmsBaseCatalog2> pmsCatalog2s = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        return pmsCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(Long catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
        return pmsBaseCatalog3s;
    }
}
