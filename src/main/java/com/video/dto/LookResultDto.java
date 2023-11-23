package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lenovo
 * @version 1.0
 * 说明： 查询结果
 * @ClassName LookResult
 * @date 2022/9/29 22:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LookResultDto implements Serializable {
    private Integer id;
    private String userId;
    private String videoName;
    private String introduction;
    private String allCollections;
    private String director;
    private String actors;
    private String typeName;
    private String cover;
    private String fragment;
}
