package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.FollowDto;
import com.video.pojo.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zrq
 * @ClassName FollowMapper
 * @date 2022/10/7 19:40
 * @Description TODO
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
    Page<FollowDto> getFans(@Param("id") Integer id, @Param("page") Page<FollowDto> page);


    Page<FollowDto> getFollowList(@Param("id") Integer id, @Param("page") Page<FollowDto> page);
}
