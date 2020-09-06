package com.cesela.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.cesela.gmall.bean.*;
import com.cesela.gmall.manage.service.mapper.*;
import com.cesela.gmall.service.SkuService;
import com.cesela.gmall.util.RedisUtil;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @Date:2020/7/21 15:52
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }


    public PmsSkuInfo getSkuByIdFromDB(String skuId) {

        //商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfos = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> skuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfos.setSkuImageList(skuImages);
        return skuInfos;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId, String ip) {
        System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "进入的商品详情的请求");
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        JedisPool jedisPool = null;
        Jedis jedis = null;
        // 链接缓存
        try {
            jedis = redisUtil.getJedis();
            // 查询缓存
            String skuKey = "sku:" + skuId + ":info";
            String skuJson = jedis.get(skuKey);

            if (StringUtils.isNotBlank(skuJson)) {//if(skuJson!=null&&!skuJson.equals(""))
                System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "从缓存中获取商品详情");
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            } else {
                // 如果缓存中没有，查询mysql
                System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "发现缓存中没有，申请缓存的分布式锁：" + "sku:" + skuId + ":lock");

                // 设置分布式锁
                String token = UUID.randomUUID().toString();
                String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);// 拿到锁的线程有10秒的过期时间
                if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                    // 设置成功，有权在10秒的过期时间内访问数据库
                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "有权在10秒的过期时间内访问数据库：" + "sku:" + skuId + ":lock");

                    pmsSkuInfo = getSkuByIdFromDB(skuId);

                    try {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (pmsSkuInfo != null) {
                        // mysql查询结果存入redis
                        jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                    } else {
                        // 数据库中不存在该sku
                        // 为了防止缓存穿透将，null或者空字符串值设置给redis
                        jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                    }

                    // 在访问mysql后，将mysql的分布锁释放
                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "使用完毕，将锁归还：" + "sku:" + skuId + ":lock");
                    String lockToken = jedis.get("sku:" + skuId + ":lock");
                    if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)) {
                        //jedis.eval("lua");可与用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                        jedis.del("sku:" + skuId + ":lock");// 用token确认删除的是自己的sku的锁
                    }
                } else {
                    // 设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "没有拿到锁，开始自旋");
                    return getSkuById(skuId, ip);
                }
            }
        } finally {
            //关闭Jedis对象,把从Pool中获取的Jedis放回到Pool,供其它请求使用
            if (jedis != null) {
                jedis.close();
            }
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {

        List<PmsSkuInfo> skuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);

        return skuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllPmsSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {
        boolean b = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        BigDecimal price = pmsSkuInfo1.getPrice();
        if (price.compareTo(productPrice) == 0) {
            b = true;
        }
        return b;
    }


}
