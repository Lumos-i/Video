package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zrq
 * @ClassName TypeListDto
 * @date 2022/10/6 16:51
 * @Description 类型返回结果封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeListDto {
    private Integer id;
    private String typeName;
    private Integer typeLevel;
    private Integer typeClassify;
    private List<TypeClassify> childType;
    private List<TypeClassify> countryType;
    private List<TypeClassify> authorType;
    private List<TypeClassify> yearType;
    private List<String> vipType;
}
