package com.video.service;

import com.video.utils.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zrq
 * @ClassName RemindService
 * @date 2022/10/8 20:35
 * @Description TODO
 */
@SuppressWarnings("rawtypes")
public interface RemindService {
    BaseResponse getRemind(HttpServletRequest request, Integer remindType, Integer nodePage, Integer pageSize);

    BaseResponse getRemindNums(HttpServletRequest request, Integer remindType);

    BaseResponse deleteRemind(List<Integer> ids);

    BaseResponse sendRemind(String content);


    BaseResponse addRemind(Integer toId, Integer id, String message, String otherId, Integer remindType);

    BaseResponse getSystemNoticePage(Integer nodePage, Integer pageSize);

    BaseResponse deleteSystemNotice(Integer id);
}
