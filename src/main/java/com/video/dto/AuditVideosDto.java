package com.video.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditVideosDto {//待审核视频表
    @ApiModelProperty("视频id")
    private Integer id;
    @ApiModelProperty("视频名称")
    private String videoName;
    @ApiModelProperty("创作者id")
    private Integer userId;
    @ApiModelProperty("视频的集数")
    private String collection;
    @ApiModelProperty("视频的状态")
    private Integer videoStatus;
    @ApiModelProperty("视频的类型")
    private String typeName;
    @ApiModelProperty("视频是否为vip:1 是,0 不是")
    private Integer vip;
    @ApiModelProperty("花絮名")
    private String sidelightsName;
    @ApiModelProperty("视频的地址")
    private String address;
}
