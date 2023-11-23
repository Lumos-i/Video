package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("vip_price")
public class VipPrice{
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Integer id;

    @TableField("vip_days")
    @ApiModelProperty("vip天数")
    private Integer vipDays;

    @TableField("vip_price")
    @ApiModelProperty("vip价格")
    private Float vipPrice;

    @TableField("vip_points")
    @ApiModelProperty("积分")
    private Integer vipPoints;
}
