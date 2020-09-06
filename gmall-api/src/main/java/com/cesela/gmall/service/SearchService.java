package com.cesela.gmall.service;

import com.cesela.gmall.bean.PmsSearchParam;
import com.cesela.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * @Date:2020/8/4 21:06
 */

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
