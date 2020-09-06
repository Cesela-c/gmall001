package com.cesela.gmall.service;

import com.cesela.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date:2020/7/21 15:50
 */

public interface SkuService {

    /**
     * 保存sku信息
     * @param pmsSkuInfo
     * @return
     */
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 查询商品信息
     * @param skuId
     * @return
     */
    PmsSkuInfo getSkuById(String skuId,String ip);



    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    /**
     * 查询所有商品信息
     */
    List<PmsSkuInfo> getAllPmsSku(String catalog3Id);

    /**
     * 订单价格校验
     * @return
     */
    boolean checkPrice(String productSkuId, BigDecimal productPrice);
}
