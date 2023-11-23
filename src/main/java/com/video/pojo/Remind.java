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
 * @author zrq
 * @ClassName Remind
 * @date 2022/10/8 20:27
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("remind")
@ApiModel("消息提醒表")
public class Remind implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("消息id")
    private Integer id;
    @TableField("u_id")
    @ApiModelProperty("被提醒人id")
    private Integer uId;
    @TableField("content")
    @ApiModelProperty("提醒内容")
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField("main_id")
    @ApiModelProperty("其他id 点赞为点赞人的id,收藏为收藏人的id,通过审核为视频的id")
    private String mainId;
    @TableField("remind_type")
    @ApiModelProperty("提醒类型 0(官方提醒) 1(收藏提醒) 2（评论提醒） 3(关注提醒)")
    private Integer remindType;
    @TableField("remind_status")
    @ApiModelProperty("提醒状态 0(未读) 1(已读)")
    private Integer remindStatus;
    @TableField("send_uid")
    @ApiModelProperty("接收消息的用户id")
    private Integer sendUid;
    @TableField("delete_id")
    @ApiModelProperty("接收消息的用户id")
    private String deleteId;

}
