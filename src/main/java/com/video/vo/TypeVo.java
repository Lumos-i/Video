package com.video.vo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zrq
 * @ClassName TypeVo
 * @date 2022/11/3 21:26
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeVo {
    @ApiModelProperty("类型名")
    private String typeName;
    @ApiModelProperty("子级类型id 1 子级分类 2 国家分类 3 制作方分类 4 年份分类 5 是否付费")
    private Integer typeClassify;
    @ApiModelProperty("等级")
    private Integer typeLevel;
    @ApiModelProperty("上级id")
    private Integer lastId;
}
