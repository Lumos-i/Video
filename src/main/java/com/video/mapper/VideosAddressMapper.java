package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.AllCollectionsDto;
import com.video.dto.AuditVideosDto;
import com.video.pojo.VideosAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideosAddressMapper extends BaseMapper<VideosAddress> {

    Integer deleteBatchVideoIds(@Param("ids")List<Integer> ids);

    Page<AllCollectionsDto> selectAddressId(@Param("videoId")Integer videoId,
                                            @Param("page")Page<AllCollectionsDto> page,
                                            @Param("status") Integer status);

    Page<AuditVideosDto> getAuditVideos(Page<AuditVideosDto> page);

    List<AllCollectionsDto> selectAddressId2(@Param("videoId") Integer videoId,@Param("status") Integer status);

    int updateVideoAddress(@Param("id") Integer id, @Param("collections") String collections,@Param("newAddress") String newAddress,@Param("sidelightsName") String sidelightsName,@Param("videoType") String videoType,@Param("vip") Integer vip);
    Page<AuditVideosDto> getAuditVideos(@Param("page") Page<AuditVideosDto> page, @Param("status") Integer status);
}
