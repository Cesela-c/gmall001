package com.cesela.gmall.service;

import com.cesela.gmall.bean.OmsOrder;

/**
 * @Date:2020/8/31 10:09
 */

public interface OrderService {

    /**
     * 检查交易代码
     * @param memberId
     * @return
     */
    String checkTradeCode(String memberId, String tradeCoe);



    /**
     * 生成校验码,为了在提交订单时做交易码的检验
     * @param memberId
     * @return
     */
    String getTradeCode(String memberId);


    /**
     * 将订单和订单详情写入数据库
     * 删除购物车的对应商品
     * @param omsOrder
     */
    void saveOrder(OmsOrder omsOrder);


    /**
     * 通过交易编号生成支付信息
     * @param outTradeNo
     * @return
     */
    OmsOrder getOrderOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
