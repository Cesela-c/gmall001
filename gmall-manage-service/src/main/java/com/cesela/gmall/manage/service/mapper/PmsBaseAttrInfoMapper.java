package com.cesela.gmall.manage.service.mapper;

import com.cesela.gmall.bean.PmsBaseAttrInfo;
import com.cesela.gmall.bean.PmsProductInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Date:2020/7/5 22:17
 */

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    List<PmsBaseAttrInfo> selectAttrValueListByValueId(@Param("valueIdStr") String valueIdStr);
}
