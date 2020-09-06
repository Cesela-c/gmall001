package com.cesela.gmall.service;

import com.cesela.gmall.bean.PmsBaseCatalog1;
import com.cesela.gmall.bean.PmsBaseCatalog2;
import com.cesela.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @Date:2020/7/4 21:31
 */

public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(Long catalog2Id);
}
