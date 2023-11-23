package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lenovo
 * @ClassName TopDto
 * @date 2022/10/3 14:50
 * @Description 排行耪信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopDto implements Serializable {
    private Integer id;
    private String videoName;
    private String introduction;
    private String allCollections;
    private String director;
    private String actors;
    private String address;
    private String collection;
    private String cover;
    private String fragment;
}
