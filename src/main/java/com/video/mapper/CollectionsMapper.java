package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.CollectionInfoDto;
import com.video.pojo.Collections;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zrq
 * @ClassName CollectionsMapper
 * @date 2022/10/5 9:56
 * @Description TODO
 */
@Mapper
public interface CollectionsMapper extends BaseMapper<Collections> {
    Page<CollectionInfoDto> getCollections(@Param("id") Integer id, @Param("page") Page<CollectionInfoDto> page);
}
