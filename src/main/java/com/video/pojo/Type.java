package com.video.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: Type
 * @author: 赵容庆
 * @date: 2022年09月22日 16:49
 * @Description: TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "type",autoResultMap = true)
@ApiModel("类别表")
public class Type implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("视频id")
    private Integer id;
    @TableField("type_name")
    @ApiModelProperty("类别")
    private String typeName;
    @TableField("type_classify")
    @ApiModelProperty("类别")
    private Integer typeClassify;
    @TableField("type_level")
    @ApiModelProperty("类别等级")
    private Integer typeLevel;
    @TableField("last_id")
    @ApiModelProperty("类别等级")
    private Integer lastId;
}
