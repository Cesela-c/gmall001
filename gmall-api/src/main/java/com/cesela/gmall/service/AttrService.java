package com.cesela.gmall.service;

import com.cesela.gmall.bean.PmsBaseAttrInfo;
import com.cesela.gmall.bean.PmsBaseAttrValue;
import com.cesela.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * @Date:2020/7/5 22:12
 */

public interface AttrService {
    /**
     * 查询物品属性
     * @param catalog3Id
     * @return
     */
    List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id);

    /**
     * 保存添加属性值
     * @param pmsBaseAttrInfo
     * @return
     */
    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 查询修改后的数据
     * @param attrId
     * @return
     */
    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    /**
     * 添加销售属性
     */
    List<PmsBaseSaleAttr> getBaseSaleAttr();

    /**
     * 根据valueId将属性列表查询出来
     * @param valueIdSet
     * @return
     */
    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet);
}
