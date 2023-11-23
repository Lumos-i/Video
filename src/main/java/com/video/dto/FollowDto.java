package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName FollowDto
 * @date 2022/10/7 20:11
 * @Description 关注信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private String photo;
    private Integer followNums;
    private Integer fanNums;
}
