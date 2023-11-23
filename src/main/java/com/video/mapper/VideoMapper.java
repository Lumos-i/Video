package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.LookResultDto;
import com.video.dto.ShowVideoDto;
import com.video.dto.TopDto;
import com.video.pojo.Videos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Videos> {
    Page<ShowVideoDto> showVideos(@Param("typeName") String typeName,@Param("page")Page page);

    Page<LookResultDto> lookVideo(@Param("page")Page<LookResultDto> page, @Param("text") String text);

    List<TopDto> getTop(Integer id);

    int updateVideo(@Param("id") Integer id, @Param("videoName") String videoName, @Param("director") String director,
                    @Param("introduction") String introduction, @Param("actors") String actors,
                    @Param("typeName") String typeName, @Param("allCollections") String allCollections,
                    @Param("newCover") String newCover, @Param("newFragment") String newFragment);
}
