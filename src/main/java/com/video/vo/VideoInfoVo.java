package com.video.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zrq
 * @ClassName VideoInfo
 * @date 2022/11/3 21:44
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfoVo {
    @ApiModelProperty("视频名")
    private String videoName;
    @ApiModelProperty("封面")
    private MultipartFile cover;
    @ApiModelProperty("悬浮视频")
    private MultipartFile fragment;
    @ApiModelProperty("简介")
    private String introduction;
    @ApiModelProperty("演员")
    private String actors;
    @ApiModelProperty("导演")
    private String director;
    @ApiModelProperty("类型名")
    private String typeName;
    @ApiModelProperty("总集数")
    private String allCollections;
}
