package com.cesela.gmall.service;

import com.cesela.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * @Date:2020/8/20 11:36
 */

public interface CartService {
    OmsCartItem ifCartByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> cartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
