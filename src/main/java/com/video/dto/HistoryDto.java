package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lenovo
 * @ClassName History
 * @date 2022/10/3 17:32
 * @Description 历史记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto implements Serializable {
    private Integer id;
    private Integer videoId;
    private Integer videoAddressId;
    private String videoName;
    private String cover;
    private String fragment;
    private String collection;
    private String progress;
    private String sidelightsName;
}
