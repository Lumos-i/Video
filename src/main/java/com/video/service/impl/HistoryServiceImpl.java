package com.video.service.impl;

import com.video.dto.OneHistory;
import com.video.mapper.HistoryMapper;
import com.video.service.HistoryService;
import com.video.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zrq
 * @ClassName HistoryServiceImpl
 * @date 2022/10/4 16:37
 * @Description TODO
 */
@Service
@Transactional
@SuppressWarnings("rawtypes")
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    private HistoryMapper historyMapper;
    @Override
    public BaseResponse getOneHistory(Integer id) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        OneHistory oneHistory = historyMapper.getOneHistory(id);
        return oneHistory == null ? BaseResponse.success("没有数据") : BaseResponse.success(oneHistory);
    }
}
