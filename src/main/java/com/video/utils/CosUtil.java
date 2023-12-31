package com.video.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.PutObjectRequest;
import com.video.config.CosConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Random;

@Service
@Slf4j
@Qualifier("cosUtil")
public class CosUtil {
    @Autowired
    private COSClient cosClient;
    @Autowired
    private CosConfig cosConfig;

    private final String PHOTO = "https://video-1311156839.cos.ap-beijing.myqcloud.com/photo/20221006111627_1705032704.png";
    String type = ".mp4、.MP4、.jpg、.jpeg、.png、.gif、.webp、.JPG、.JPEG、.PNG、.WEBP";

    public String upload(MultipartFile file, Integer types) {
        String url = "";
        try {
            String originalfileName = file.getOriginalFilename();

            // 获得文件流
            InputStream inputStream = file.getInputStream();


            //设置文件key
            String filePath = getFileKey(originalfileName,types);

            if(filePath.equals("格式不正确！")){
                return filePath;
            }
            // 上传文件
            cosClient.putObject(new PutObjectRequest(cosConfig.getBucketName(), filePath, inputStream, null));
            cosClient.setBucketAcl(cosConfig.getBucketName(), CannedAccessControlList.PublicRead);

            url = cosConfig.getPath() + "/" +filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (url.contains("格式不正确")) {
            return "格式不正确";
        }else {
            return url;
        }
    }

    private String getFileKey(String originalfileName,Integer types) {
        String filePath = "";
        if (types == 1) {
            filePath = "static/";
        }else {
            filePath = "photo/";
        }
        //1.获取后缀名
        String fileType = originalfileName.substring(originalfileName.lastIndexOf("."));
        if (!type.contains(fileType)) {
            return "格式不正确！";
        }
        //2.去除文件后缀 替换所有特殊字符
//        String fileStr = StrUtil.removeSuffix(originalfileName, fileType).replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5]", "_");
        String fileStr = String.valueOf((int)(Math.random()*9+1)*1000000000);
        filePath += new DateTime().toString("yyyyMMddHHmmss") + "_" + fileStr + fileType;
        return filePath;
    }


    public void deleteObject (String key,Integer types) throws CosClientException {
        try {
            if(PHOTO.equals(key)) {
                return;
            }
            int l = key.length();
            String a ;
            if(types==1){
                a = key.substring(key.indexOf("static"),l);
            }else {
                a = key.substring(key.indexOf("photo"),l);
            }
            cosClient.deleteObject(cosConfig.getBucketName(), a);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }
    }
}