package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName TypeClassify
 * @date 2022/10/6 17:00
 * @Description 类型分类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeClassify implements Serializable {
    private Integer id;
    private String typeName;
    private Integer typeLevel;
    private Integer typeClassify;
}
