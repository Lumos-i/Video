package com.video.service;

import com.video.utils.BaseResponse;
@SuppressWarnings("rawtypes")
public interface VipPriceService {
    BaseResponse addVipPrice(Integer vipDays, Float vipPrice,Integer points);

    BaseResponse deleteVipPrice(Integer id);

    BaseResponse updateVipPrice(Integer vipDays, Float vipPrice,Integer points);

    BaseResponse selectVipPriceList();
}
