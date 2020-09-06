package com.cesela.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cesela.gmall.bean.PmsProductImage;
import com.cesela.gmall.bean.PmsProductInfo;
import com.cesela.gmall.bean.PmsProductSaleAttr;
import com.cesela.gmall.service.SpuService;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Date:2020/7/8 17:41
 */
@Controller
@CrossOrigin //跨域
public class SpuController {

    @Reference
    private SpuService spuService;

    @RequestMapping(value = "/spuList")
    public @ResponseBody List<PmsProductInfo> spuList(String catalog3Id) {
        List<PmsProductInfo> spuList = spuService.getSpuList(catalog3Id);
        return spuList;
    }

    @RequestMapping(value = "/saveSpuInfo")
    public @ResponseBody String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {

        spuService.saveSpuInfo(pmsProductInfo);

        return "seccess";
        /*
        用户点击保存spu信息(saveSpuInfo)
        将spu的基本信息,销售属性列表,图片地址列表的元数据保存到后台
     */
    }

    @Value("http://192.168.157.128")
    String fileUrl;

    @RequestMapping(value = "/fileUpload")
    public @ResponseBody String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        /*String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);*/
        String imgUrl = fileUrl;
        if (file != null) {
            System.out.println("multipartFile = " + file.getName() + "|" + file.getSize());

            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String filename = file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(filename, ".");

            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            imgUrl = fileUrl;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl += "/" + path;
            }

        }
        return imgUrl;
    }

    //销售平台属性
    @RequestMapping(value = "/spuSaleAttrList")
    public @ResponseBody List<PmsProductSaleAttr> pmsProductSaleAttrs(String spuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }

    @RequestMapping(value = "/spuImageList")
    public @ResponseBody List<PmsProductImage> spuImageList(String spuId) {
        List<PmsProductImage> pmsProductImageList = spuService.getSpuImageList(spuId);
        return pmsProductImageList;
    }

}
