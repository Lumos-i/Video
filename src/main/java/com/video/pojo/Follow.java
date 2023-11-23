package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName Follow
 * @date 2022/10/7 19:37
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("follow")
@ApiModel("关注表")
public class Follow implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("关注记录id")
    private Integer id;
    @TableField("main_id")
    @ApiModelProperty("关注人的id")
    private Integer mainId;
    @TableField("u_id")
    @ApiModelProperty("被关注人的id")
    private Integer uId;
}
