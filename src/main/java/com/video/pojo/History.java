package com.video.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: History
 * @author: 赵容庆
 * @date: 2022年09月22日 16:34
 * @Description: TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("history")
@ApiModel("视频历史记录表")
public class History implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("历史记录id")
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    @TableField("video_id")
    @ApiModelProperty("视频id")
    private Integer videoId;
    @TableField("video_address_id")
    @ApiModelProperty("视频地址id")
    private Integer videoAddressId;
    @TableField("progress")
    @ApiModelProperty("视频时长")
    private String progress;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
