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

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment_report")
@ApiModel("评论举报表")
public class CommentReport {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("举报id")
    private Integer id;

    @TableField("comment_id")
    @ApiModelProperty("评论id")
    private Integer commentId;

    @TableField("report_user_id")
    @ApiModelProperty("举报的用户id")
    private Integer reportUserId;

    @TableField("comment_user_id")
    @ApiModelProperty("被举报的评论的发布者id")
    private Integer commentUserId;

    @TableField("report_cause")
    @ApiModelProperty("举报原因")
    private String reportCause;

    @TableField("report_time")
    @ApiModelProperty("举报时间")
    private LocalDateTime reportTime;

    @TableField("report_status")
    @ApiModelProperty("举报状态")
    private Integer reportStatus;

    @TableField("dispose_time")
    @ApiModelProperty("管理员处理时间")
    private LocalDateTime disposeTime;
}
