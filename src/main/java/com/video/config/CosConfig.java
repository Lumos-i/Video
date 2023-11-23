package com.video.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;


/**
 * @ClassName: CosConfig
 * @author: 赵容庆
 * @date: 2022年09月22日 10:45
 * @Description: cos云配置
 */

@Configuration
@ConditionalOnProperty(prefix = "cos.tengxun",name = "enable",havingValue = "true")
@ConfigurationProperties(prefix = "cos.tengxun")
public class CosConfig implements Serializable {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String path;
    private Boolean enable;


    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Bean
    public COSClient cosClient(){
        //初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(this.secretId, this.secretKey);
        //设置 bucket 的区域
        Region region = new Region(this.region);
        ClientConfig clientConfig = new ClientConfig(region);
        //生成 cos 客户端
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }
}
