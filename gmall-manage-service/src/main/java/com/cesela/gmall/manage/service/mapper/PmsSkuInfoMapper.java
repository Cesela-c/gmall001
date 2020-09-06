package com.cesela.gmall.manage.service.mapper;

import com.cesela.gmall.bean.PmsSkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Date:2020/7/21 16:17
 */

public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {

    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(String productId);
}
