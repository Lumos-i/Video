package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentReplyDto {
    private Integer id;//评论id
    private Integer userId;//用户id
    private Integer videoId;//视频id
    private Integer commentLevel;//评论等级
    private Integer commentId;//评论id
    private String comment;//评论内容
    private LocalDateTime commentTime;//评论时间
    private String commentLove;//评论点赞量
    private String userName;//用户昵称
    private String userPhoto;//用户头像
}
