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

/**
 * @ClassName: FeedBack
 * @author: 赵容庆
 * @date: 2022年09月22日 16:30
 * @Description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("feedback")
@ApiModel("评论表")
public class FeedBack implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("反馈id")
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    @TableField("admin_id")
    @ApiModelProperty("管理员id")
    private Integer adminId;
    @TableField("video_id")
    @ApiModelProperty("视频id")
    private Integer videoId;
    @TableField("content")
    @ApiModelProperty("反馈内容")
    private String content;
    @TableField("telephone")
    @ApiModelProperty("联系电话")
    private String telephone;
    @TableField("feedback_time")
    @ApiModelProperty("添加反馈的时间")
    private LocalDateTime feedBackTime;
}
