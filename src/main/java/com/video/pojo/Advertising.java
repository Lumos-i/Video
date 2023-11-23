package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@TableName("advertising")
@ApiModel("广告表")
public class Advertising implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("广告id")
    private Integer id;
    @TableField("advertising_photo")
    @ApiModelProperty("广告封面")
    private String advertisingPhoto;
    @TableField("advertising_address")
    @ApiModelProperty("广告地址")
    private String advertisingAddress;
    @TableField("admin_id")
    @ApiModelProperty("管理员id")
    private Integer adminId;
    @TableField("weight")
    @ApiModelProperty("权重")
    private double weight;
}
