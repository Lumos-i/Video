package com.video.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName Advertising
 * @date 2022/10/6 9:40
 * @Description 广告实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingVo implements Serializable {
    @ApiModelProperty("广告封面")
    private MultipartFile cover;
    @ApiModelProperty("广告地址")
    private MultipartFile address;
    @ApiModelProperty("管理员id")
    private Integer adminId;
    @ApiModelProperty("权重")
    private double weight;
}
