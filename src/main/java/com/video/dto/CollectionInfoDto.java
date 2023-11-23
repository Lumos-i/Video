package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName CollectionInfo
 * @date 2022/10/5 15:39
 * @Description 收藏的相关信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionInfoDto implements Serializable {
    private Integer cId;
    private Integer id;
    private String videoName;
    private String cover;
    private String fragment;
}
