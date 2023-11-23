package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lenovo
 * @ClassName TypeDto
 * @date 2022/10/3 15:41
 * @Description 类型信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeDto implements Serializable {
    private Integer parentId;
    private Integer childId;
    private String mainType;
    private String typeName;
}
