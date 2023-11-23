package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zrq
 * @ClassName VideoDetailDto
 * @date 2022/10/9 21:52
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDetailDto implements Serializable {
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
    private Date createTime;
    private Integer status;
}
