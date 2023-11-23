package com.video.service;

import com.video.utils.BaseResponse;
import com.video.vo.VideoVo;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface VideosAddressService {
    BaseResponse addVideosAddress(VideoVo videoVo);

    BaseResponse getAddress(Integer videoId, Integer videoAddressId);

    BaseResponse deleteVideoAddress(List<Integer> ids);

    BaseResponse updateVip(List<Integer> ids, List<Integer> vips);

    BaseResponse updateStatus(Integer id, Integer status);

    BaseResponse getAddressId(Integer nodePage, Integer pageSize, Integer videoId, Integer status);

    BaseResponse getHighlights(Integer videoId, Integer nodePage, Integer pageSize);

    BaseResponse getAddressId2(Integer videoId,Integer status);

    BaseResponse getsidelights(Integer id);

    BaseResponse getAddress2(Integer id);
}
