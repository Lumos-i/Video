package com.video.service;

import com.video.utils.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zrq
 * @ClassName Collections
 * @date 2022/10/5 9:52
 * @Description TODO
 */

@SuppressWarnings("rawtypes")
public interface CollectionsService {
    BaseResponse collectVideo(Integer videoId);

    BaseResponse deleteCollections(List<Integer> ids);

    BaseResponse haveCollected(Integer id, Integer videoId);


}
