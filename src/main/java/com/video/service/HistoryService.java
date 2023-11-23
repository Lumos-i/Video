package com.video.service;

import com.video.utils.BaseResponse;

/**
 * @author zrq
 * @ClassName HistoryService
 * @date 2022/10/4 16:37
 * @Description TODO
 */
@SuppressWarnings("rawtypes")
public interface HistoryService {
    BaseResponse getOneHistory(Integer id);
}
