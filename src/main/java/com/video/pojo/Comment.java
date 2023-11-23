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
 * @ClassName: Comment
 * @author: 赵容庆
 * @date: 2022年09月22日 16:23
 * @Description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
@ApiModel("评论表")
public class Comment implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("评论id")
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    @TableField("video_id")
    @ApiModelProperty("视频id")
    private Integer videoId;
    @TableField("comment_level")
    @ApiModelProperty("评论等级")
    private Integer commentLevel;
    @TableField("comment_id")
    @ApiModelProperty("评论id")
    private Integer commentId;
    @TableField("comment")
    @ApiModelProperty("评论")
    private String comment;
    @TableField("comment_time")
    @ApiModelProperty("评论时间")
    private LocalDateTime commentTime;
    @TableField("comment_love")
    @ApiModelProperty("评论点赞量")
    private String commentLove;
}
