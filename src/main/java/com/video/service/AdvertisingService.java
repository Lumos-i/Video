package com.video.service;

import com.video.utils.BaseResponse;
import com.video.vo.AdvertisingVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author zrq
 * @ClassName AdvertisingService
 * @date 2022/10/6 9:48
 * @Description TODO
 */
@SuppressWarnings("rawtypes")
public interface AdvertisingService {
    BaseResponse addAdvertising(AdvertisingVo advertisingVo);

    BaseResponse deleteAdvertising(List<Integer> ids);

    BaseResponse getAllAdvertising(Integer nodePage, Integer pageSize);

    BaseResponse getSomeAdvertising(Integer num);
}
