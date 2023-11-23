package com.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;//应用id
    private String appPrivateKey;//私钥
    private String alipayPublicKey;//公钥
    private String notifyUrl;//回调地址
    private String contentKey;//AES密钥
}
