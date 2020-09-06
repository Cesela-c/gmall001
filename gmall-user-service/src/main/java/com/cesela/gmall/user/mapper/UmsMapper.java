package com.cesela.gmall.user.mapper;

import com.cesela.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Date:2020/6/27 9:54
 */

public interface UmsMapper extends Mapper<UmsMember> {
    /**
     * 查询所有用户信息
     * @return
     */
    List<UmsMember> selectAllUser();
}
