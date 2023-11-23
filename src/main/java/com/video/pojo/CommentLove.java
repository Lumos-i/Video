package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("comment_love")
public class CommentLove {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("点赞的用户id")
    private Integer userId;
    @TableField("comment_id")
    @ApiModelProperty("点赞的评论id")
    private Integer commentId;
    @TableField("video_id")
    @ApiModelProperty("评论的视频id")
    private Integer videoId;
}
