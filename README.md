# Video

#### 介绍
个人开发的视频项目

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx


yml配置
```java
server:
  port: 8555
  servlet:
    context-path: /
    session:
      # session失效时间2小时
      timeout: 10800
# 数据源配置
spring:
  datasource:
    # 数据库配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://******:3306/video?userUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: 
    password: 
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      max-active: 20
      initial-size: 5
      max-wait: 6000
      min-idle: 5
  mvc:
    static-path-pattern: /**
    hiddenmethod:
      filter:
        enabled: true
  jackson:
    default-property-inclusion: non_null  #Jason处理时忽略空值
  #redis配置
  redis:
    host: 
    port: 6379
    password: 
#mybatis-plus配置
mybatis-plus:
  type-aliases-package: com.video.pojo
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
#    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #开启sql日志



cos:
  tengxun:
    secretId: 
    secretKey: 
    region: 
    path: 
    bucketName: 
    enable: true

alipay:
  appId: 
  appPrivateKey: 
  alipayPublicKey: 
  notifyUrl:  
  contentKey: 
```

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
