package com.cesela.gmall.service;

import com.cesela.gmall.bean.PmsProductImage;
import com.cesela.gmall.bean.PmsProductInfo;
import com.cesela.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @Date:2020/7/8 18:10
 */

public interface SpuService {
    /**
     * 通过id查询所有商品信息
     * @return
     */
    List<PmsProductInfo> getSpuList(String catalog3Id);



    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    /**
     * 根据id查询销售属性
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);


    /**
     * 查询图片
     * @param spuId
     * @return
     */
    List<PmsProductImage> getSpuImageList(String spuId);


    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
