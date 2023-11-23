package com.video.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.video.utils.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: VideosAddress
 * @author: 赵容庆
 * @date: 2022年09月23日 11:36
 * @Description: TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "videos_address",autoResultMap = true)
@ApiModel("视频地址表")
public class VideosAddress implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("视频地址id")
    private Integer id;
    @TableField("video_id")
    @ApiModelProperty("视频id")
    private Integer videoId;
    @TableField("collection")
    @ApiModelProperty("集数")
    private String collection;
    @TableField("address")
    @ApiModelProperty("视频储存地址")
    private String address;
    @TableField("video_type")
    @ApiModelProperty("视频类别")
    private String videoType;
    @TableField("sidelights_name")
    @ApiModelProperty("花絮名")
    private String sidelightsName;
    @TableField("vip")
    @ApiModelProperty("是否为vip视频")
    private Integer vip;
    @TableField("video_status")
    @ApiModelProperty("视频审核状态")
    private Integer videoStatus;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date  updateTime;

    public VideosAddress(Integer id, String collection, String address, String videoType, Integer vip) {
        this.id = id;
        this.collection = collection;
        this.address = address;
        this.videoType = videoType;
        this.vip = vip;
    }
}
