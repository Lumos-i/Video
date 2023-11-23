package com.video.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zrq
 * @ClassName UserInfoVo
 * @date 2022/11/3 21:38
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("生日")
    private String birth;
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("地址")
    private String address;
}
