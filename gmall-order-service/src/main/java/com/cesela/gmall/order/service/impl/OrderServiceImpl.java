package com.cesela.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.cesela.gmall.bean.Constants;
import com.cesela.gmall.bean.OmsOrder;
import com.cesela.gmall.bean.OmsOrderItem;
import com.cesela.gmall.mq.ActiveMQUtil;
import com.cesela.gmall.order.mapper.OmsOrderItemMapper;
import com.cesela.gmall.order.mapper.OmsOrderMapper;
import com.cesela.gmall.service.CartService;
import com.cesela.gmall.service.OrderService;
import com.cesela.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @Date:2020/8/31 10:24
 */

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Reference
    private CartService cartService;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String tradeKey = Constants.user + memberId + Constants.tradeCode;
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));
            if (eval != null && eval != 0) {
                //jedis.del(tradeKey);
                return "success";
            } else {
                return "fail";
            }
        } finally {
            jedis.close();
        }

    }

    @Override
    public String getTradeCode(String memberId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String tradeKey = Constants.user + memberId + Constants.tradeCode;
            String tradeCode = UUID.randomUUID().toString();
            // 15分钟过期时间
            jedis.setex(tradeKey, 60 * 15, tradeCode);
            return tradeCode;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        // 保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        // 保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
            // cartService.delCart;
        }
    }

    @Override
    public OmsOrder getOrderOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);
        return omsOrder1;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example e = new Example(OmsOrder.class);
        e.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());

        OmsOrder omsOrderUpdate = new OmsOrder();

        omsOrderUpdate.setStatus("1");

        // 发送一个订单已支付的队列，提供给库存消费
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payhment_success_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(payhment_success_queue);
            TextMessage textMessage = new ActiveMQTextMessage();//字符串文本
            //MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            // 查询订单的对象,转化为json字符串,存入ORDER_PAY_QUEUE队列
            OmsOrder omsOrderParam = new OmsOrder();
            omsOrderParam.setOrderSn(omsOrder.getOrderSn());
            OmsOrder omsOrderResponse = omsOrderMapper.selectOne(omsOrderParam);

            OmsOrderItem omsOrderItemParam = new OmsOrderItem();
            omsOrderItemParam.setOrderSn(omsOrderParam.getOrderSn());
            List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.select(omsOrderItemParam);
            omsOrderResponse.setOmsOrderItems(omsOrderItems);
            textMessage.setText(JSON.toJSONString(omsOrderResponse));

            omsOrderMapper.updateByExampleSelective(omsOrderUpdate, e);
            producer.send(textMessage);
            session.commit();
        } catch (Exception ex) {
            // 消息回滚
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }

    }
}
