package com.cesela.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cesela.gmall.bean.PmsBaseAttrInfo;
import com.cesela.gmall.bean.PmsBaseAttrValue;
import com.cesela.gmall.bean.PmsBaseSaleAttr;
import com.cesela.gmall.manage.service.mapper.PmsBaseAttrInfoMapper;
import com.cesela.gmall.manage.service.mapper.PmsBaseAttrValueMapper;
import com.cesela.gmall.manage.service.mapper.PmsBaseSaleAttrMapper;
import com.cesela.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Date:2020/7/5 22:13
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper attrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper attrValueMapper;

    @Autowired
    private PmsBaseSaleAttrMapper saleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoMapper.select(pmsBaseAttrInfo);

        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfos) {

            List<PmsBaseAttrValue> pmsBaseAttrValues = new ArrayList<>();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();

            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            pmsBaseAttrValues = attrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValues);

        }
        return pmsBaseAttrInfos;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        String id = pmsBaseAttrInfo.getId();

        if (StringUtils.isBlank(id)) {
            //保存属性
            attrInfoMapper.insertSelective(pmsBaseAttrInfo);

            //保存属性值
            List<PmsBaseAttrValue> attrValues = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue attrValue : attrValues) {
                attrValue.setAttrId(pmsBaseAttrInfo.getId());

                attrValueMapper.insertSelective(attrValue);
            }
        } else {
            //修改属性
            Example example = new Example(PmsBaseAttrValue.class);
            example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());

            attrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);

            //修改属性值
            //按照属性id删除所有值
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());

            attrValueMapper.delete(pmsBaseAttrValue);

            //删除后，将新的值插入
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue baseAttrValue : pmsBaseAttrValues) {
                baseAttrValue.setAttrId(pmsBaseAttrInfo.getId());

                attrValueMapper.insertSelective(baseAttrValue);
            }
        }

        return "seccess";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValues;
    }

    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttr() {
        List<PmsBaseSaleAttr> selectBaseSaleAttr = saleAttrMapper.selectAll();
        return selectBaseSaleAttr;
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {

        String valueIdStr = StringUtils.join(valueIdSet, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoMapper.selectAttrValueListByValueId(valueIdStr);
        return pmsBaseAttrInfos;
    }

}
