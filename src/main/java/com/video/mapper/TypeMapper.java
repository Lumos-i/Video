package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.dto.TypeClassify;
import com.video.dto.TypeDto;
import com.video.pojo.Type;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zrq
 */
@Mapper
public interface TypeMapper extends BaseMapper<Type> {
    List<TypeDto> getType();

    List<TypeClassify> getDetailType(@Param("id") Integer id, Integer typeClassify);
}
