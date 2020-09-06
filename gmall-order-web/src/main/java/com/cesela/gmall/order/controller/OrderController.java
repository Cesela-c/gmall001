package com.cesela.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cesela.gmall.annitation.LoginRequired;
import com.cesela.gmall.bean.*;
import com.cesela.gmall.service.CartService;
import com.cesela.gmall.service.OrderService;
import com.cesela.gmall.service.SkuService;
import com.cesela.gmall.service.UmsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Date:2020/8/29 19:25
 */
@Controller
public class OrderController {

    @Reference
    private UmsService umsService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    @Reference
    private SkuService skuService;

    //结算
    @RequestMapping(value = "/toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletResponse response , HttpServletRequest request , HttpSession session , ModelMap map) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        List<UmsMemberReceiveAddress> receiveAddresses = null;
        List<OmsCartItem> omsCartItems = null;

        //收件人地址列表
        receiveAddresses = umsService.getReceiveAddressBtMemberId(memberId);
        //将购物车集合转化为页面计算清单集合
        omsCartItems = cartService.cartList(memberId);
        //将购物车列表转化为订单列表
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItems.add(omsOrderItem);
            }
        }

        BigDecimal totalAmount = getTotalAmount(omsCartItems);

        map.put("userAddressList", receiveAddresses);
        map.put("omsOrderItems", omsOrderItems);
        map.put("totalAmount", totalAmount);

        // 生成校验码,为了在提交订单时做交易码的检验
        String tradeCode = orderService.getTradeCode(memberId);
        map.put("tradeCode", tradeCode);
        return "trade";

    }
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();
            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }
        return totalAmount;
    }

    @RequestMapping(value = "/submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletResponse response, HttpServletRequest request, HttpSession session, ModelMap map) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //检查交易代码
        String success = orderService.checkTradeCode(memberId,tradeCode);
        if (success.equals("success")) {
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            OmsOrder omsOrder = new OmsOrder(); // 订单对象
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            //omsOrder.setFreightAmount(); // 运费,支付后再用
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("快点发货");

            String outTradeNo = "gmall";
            outTradeNo = outTradeNo + System.currentTimeMillis(); // 将毫秒时间戳拼接到外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date()); // 将时间字符串拼接到外部订单号

            omsOrder.setOrderSn(outTradeNo);
            UmsMemberReceiveAddress umsMemberReceiveAddress = umsService.getReceiveAddressById(receiveAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date time = c.getTime();

            omsOrder.setReceiveTime(time);
            omsOrder.setSourceType(0);
            omsOrder.setStatus("0");
            omsOrder.setTotalAmount(totalAmount);

            // 根据用户id获得要购买的商品列表(购物车),和总价格
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            // 将订单和订单详情写入数据库
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    // 获得订单详情列表
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    // 检价
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if (b == false) {
                        ModelAndView mv = new ModelAndView("tradeFail");
                        return mv;
                    }
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo); // 外部订单号，用来和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("1111111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");

                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            // 将订单和订单详情写入数据库
            // 删除购物车的对应商品
            orderService.saveOrder(omsOrder);
            // 重定向支付代码
            ModelAndView mv = new ModelAndView("redirect:http://payment.gmall.com:8087/index");
            mv.addObject("outTradeNo", outTradeNo);
            mv.addObject("totalAmount", totalAmount);
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("tradeFail");
            return mv;
        }

    }

}
