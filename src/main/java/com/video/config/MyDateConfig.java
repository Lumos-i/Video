package com.video.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lenovo
 * @version 1.0
 * @ClassName MyDateConfig
 * @date 2022/9/29 19:38
 * 说明：
 */
@Component
@Slf4j
public class MyDateConfig implements MetaObjectHandler {
    //插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入策略");
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

    //更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新策略");
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
