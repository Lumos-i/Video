package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName AllCollectionsDto
 * @date 2022/10/4 16:09
 * @Description 取出所有收藏
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllCollectionsDto implements Serializable {
    private Integer id;
    private String collection;
    private Integer vip;
    private Integer status;
    private String sidelightsName;
}
