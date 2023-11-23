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

/**
 * @ClassName: Collections
 * @author: 赵容庆
 * @date: 2022年09月22日 16:20
 * @Description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("collections")
@ApiModel("收藏表")
public class Collections implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("收藏表id")
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    @TableField("video_id")
    @ApiModelProperty("视频id")
    private Integer videoId;
}
