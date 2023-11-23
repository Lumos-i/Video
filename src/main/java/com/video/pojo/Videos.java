package com.video.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;



@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "videos",autoResultMap = true)
@ApiModel("视频表")
public class Videos implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("视频id")
    private Integer id;
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private Integer userId;
    @TableField("video_name")
    @ApiModelProperty("视频名")
    private String videoName;
    @TableField("slideshow")
    @ApiModelProperty("主页轮播")
    private Integer slideshow;
    @TableField("cover")
    @ApiModelProperty("封面")
    private String cover;
    @TableField("fragment")
    @ApiModelProperty("精彩片段")
    private String fragment;
    @TableField("introduction")
    @ApiModelProperty("视频介绍")
    private String introduction;
    @TableField("actors")
    @ApiModelProperty("演员")
    private String actors;
    @TableField("director")
    @ApiModelProperty("导演")
    private String director;
    @TableField("type_name")
    @ApiModelProperty("类别名")
    private String typeName;
    @TableField("collection_nums")
    @ApiModelProperty("收藏数量")
    private Integer collectionNums;
    @TableField("point_nums")
    @ApiModelProperty("点赞数量")
    private Integer pointNums;
    @TableField("all_collections")
    @ApiModelProperty("总集数")
    private String allCollections;
    @TableField("vip")
    @ApiModelProperty("是否为vip视频")
    private Integer vip;
    @TableField(fill = FieldFill.INSERT)
    private Date  createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;



    public Videos(Integer userId, String videoName, String director, String cover, String fragment, String introduction, String actors, String typeName, String allCollections) {
        this.userId = userId;
        this.videoName = videoName;
        this.director = director;
        this.cover = cover;
        this.fragment = fragment;
        this.introduction = introduction;
        this.actors = actors;
        this.director = director;
        this.typeName = typeName;
        this.allCollections = allCollections;
    }

    public Videos(Integer id, String videoName, String cover, String fragment, String introduction, String actors, String typeName, String allCollections) {
        this.id = id;
        this.videoName = videoName;
        this.cover = cover;
        this.fragment = fragment;
        this.introduction = introduction;
        this.actors = actors;
        this.typeName = typeName;
        this.allCollections = allCollections;
    }

    public Videos(Integer id, Integer slideshow) {
        this.id = id;
        this.slideshow = slideshow;
    }
}
