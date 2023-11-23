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
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user",autoResultMap = true)
@ApiModel("用户表")
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    @TableField(value = "username")
    @ApiModelProperty("用户名")
    private String username;
    @TableField
    @ApiModelProperty("邮箱")
    private String email;
    @TableField("password")
    @ApiModelProperty("密码")
    private String password;
    @TableField("sex")
    @ApiModelProperty("性别")
    private String sex;
    @TableField("birth")
    @ApiModelProperty("生日")
    private String birth;
    @TableField("photo")
    @ApiModelProperty("头像")
    private String photo;
    @TableField("integral")
    @ApiModelProperty("积分")
    private int integral;
    @TableField("vip")
    @ApiModelProperty("是否为VIP 0 不是 1是")
    private int vip;
    @TableField("upload_power")
    @ApiModelProperty("是否可以上传视频 0不可以 1可以")
    private Integer uploadPower;
    @TableField("user_power")
    @ApiModelProperty("是否为管理员 0不是 1是")
    private Integer power;
    @TableField("name")
    @ApiModelProperty("用户昵称")
    private String name;
    @TableField("address")
    @ApiModelProperty("用户地址")
    private String address;
    @TableField("vip_out_time")
    @ApiModelProperty("用户vip过期时间")
    private LocalDateTime vipOutTime;
    @TableField("introduction")
    @ApiModelProperty("个人简介")
    private String introduction;
    @TableField("phone")
    @ApiModelProperty("电话")
    private String phone;
    @TableField("user_state")
    @ApiModelProperty("用户账号状态 0正常 1封禁")
    private Integer userState;
    @TableField("close_time")
    @ApiModelProperty("封禁结束时间")
    private LocalDateTime closeTime;

}
