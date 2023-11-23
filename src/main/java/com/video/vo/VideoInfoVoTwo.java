package com.video.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zrq
 * @ClassName VideoInfoVoTwo
 * @date 2022/11/3 22:17
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfoVoTwo {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("视频名")
    private String videoName;
    @ApiModelProperty("导演")
    private String director;
    @ApiModelProperty("介绍")
    private String introduction;
    @ApiModelProperty("演员")
    private String actors;
    @ApiModelProperty("类型名")
    private String typeName;
    @ApiModelProperty("总集数")
    private String allCollections;
    @ApiModelProperty("封面")
    private MultipartFile cover;
    @ApiModelProperty("悬浮视频")
    private MultipartFile fragment;
}
