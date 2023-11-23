package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.HistoryDto;
import com.video.dto.ShowVideoDto;
import com.video.dto.VideoDetailDto;
import com.video.pojo.User;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<ShowVideoDto> getCollectionsList(@Param("userId") Integer userId);

    Page<HistoryDto> history(@Param("page") Page page, @Param("id") Integer id);

    Page<VideoDetailDto> getMyVideo(@Param("page") Page<VideoDetailDto> page, @Param("id") Integer id, @Param("status") Integer status);

    Page<VideoDetailDto> getMyVideo2(@Param("page")Page<VideoDetailDto> page,@Param("id") Integer id);

    List<String> searchVideoName(String text);

    List<String> searchTypeName(String text);

    List<String> searchUserName(String text);
}
