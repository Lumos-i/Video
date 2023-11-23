package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vip_orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Integer id;

    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;

    @TableField("order_on")
    @ApiModelProperty("自己生成的订单编号")
    private String orderOn;

    @TableField("order_title")
    @ApiModelProperty("订单名称")
    private String orderTitle;

    @TableField("vip_time")
    @ApiModelProperty("充值的vip天数")
    private Integer vipTime;

    @TableField("order_total")
    @ApiModelProperty("订单价格")
    private Float orderTotal;

    @TableField("order_time")
    @ApiModelProperty("订单创建时间")
    private LocalDateTime orderTime;

    @TableField("order_state")
    @ApiModelProperty("订单状态")
    private String orderState;

    @TableField("return_time")
    @ApiModelProperty("退款时间")
    private LocalDateTime returnTime;

    @TableField("alipay_on")
    @ApiModelProperty("支付宝订单编号")
    private String alipayOn;
}
