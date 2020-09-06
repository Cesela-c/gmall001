package com.cesela.gmall.service;

import com.cesela.gmall.bean.PaymentInfo;

import java.util.Map;

/**
 * @Date:2020/9/2 16:07
 */

public interface PaymentServcie {

    /**
     * 保存支付信息
     * @param paymentInfo
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新用户的支付状态
     *
     * @param paymentInfo
     */
    void updatePayment(PaymentInfo paymentInfo);

    /**
     *
     * @param out_trade_no
     * @return
     */
    Map<String, Object> checkAlipayPayment(String out_trade_no);

    /**
     *
     * @param outTradeNo
     * @param count
     */
    void sendDelayPaymentResultCheckQueue(String outTradeNo, int count);
}
