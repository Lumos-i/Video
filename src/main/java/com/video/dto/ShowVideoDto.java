package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShowVideo
 * @author: 赵容庆
 * @date: 2022年09月24日 11:14
 * @Description: 视频信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowVideoDto {
    private Integer id;
    private Integer userId;
    private String videoName;
    private String introduction;
    private String actors;
    private Integer collectionNums;
    private Integer pointNums;
    private String allCollections;
    private String typeName;
    private String cover;
    private String fragment;
}
