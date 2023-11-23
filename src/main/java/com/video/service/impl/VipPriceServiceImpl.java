package com.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.video.mapper.VipPriceMapper;
import com.video.pojo.VipPrice;
import com.video.service.VipPriceService;
import com.video.utils.BaseResponse;
import com.video.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@SuppressWarnings("rawtypes")
public class VipPriceServiceImpl implements VipPriceService {
    @Autowired
    private VipPriceMapper vipPriceMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public BaseResponse addVipPrice(Integer vipDays, Float vipPrice,Integer points) {
        if((vipPrice == null && points == null) || vipDays == null){
            return BaseResponse.error("数据为空");
        }
        if(vipDays <= 0 || vipDays > 365){
            return BaseResponse.error("数据不正确");
        }
        VipPrice vip = new VipPrice();
        vip.setVipDays(vipDays);
        if(vipPrice!=null){
            vip.setVipPrice(vipPrice);
        }
        if(points!=null){
            vip.setVipPoints(points);
        }
        int i;
        try {
            i = vipPriceMapper.insert(vip);
        }catch (Exception e){
            return BaseResponse.error("该vip天数已添加过，请勿重复添加");
        }
        if(i>0){
            redisCache.deleteObject("vipPriceList:");
            return BaseResponse.success("添加成功");
        }else {
            return BaseResponse.error("添加失败");
        }
    }

    @Override
    public BaseResponse deleteVipPrice(Integer id) {
        if(id == null || id <= 0){
            return BaseResponse.error("数据不正确");
        }
        int i;
        try {
            i = vipPriceMapper.deleteById(id);
        }catch (Exception e){
            return BaseResponse.error("删除失败，请确认信息无误后再重新删除");
        }
        if(i>0){
            redisCache.deleteObject("vipPriceList:");
            return BaseResponse.success("删除成功");
        }else {
            return BaseResponse.error("删除失败");
        }
    }

    @Override
    public BaseResponse updateVipPrice(Integer vipDays, Float vipPrice,Integer points) {
        if((vipPrice == null && points == null) || vipDays == null){
            return BaseResponse.error("数据为空");
        }
        if(vipDays <= 0 || vipDays > 365){
            return BaseResponse.error("数据不正确");
        }
        LambdaUpdateWrapper<VipPrice> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(VipPrice::getVipDays,vipDays);
        if(vipPrice!=null){
            queryWrapper.set(VipPrice::getVipPrice,vipPrice);
        }
        if(points!=null){
            queryWrapper.set(VipPrice::getVipPoints,points);
        }
        int i;
        try {
            i = vipPriceMapper.update(null, queryWrapper);
        }catch (Exception e){
            return BaseResponse.error("修改失败");
        }
        if(i>0){
            redisCache.deleteObject("vipPriceList:");
            return BaseResponse.success("修改成功");
        }else {
            return BaseResponse.error("修改失败");
        }
    }

    @Override
    public BaseResponse selectVipPriceList() {
        List<VipPrice> vipPrices = redisCache.getCacheObject("vipPriceList:");
        if(vipPrices==null){
            LambdaQueryWrapper<VipPrice> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.isNotNull(VipPrice::getVipPrice)
                    .last("order by vip_days");
            vipPrices = vipPriceMapper.selectList(queryWrapper);
            if(!vipPrices.isEmpty()){
                redisCache.setCacheObject("vipPriceList:",vipPrices,60 * 24, TimeUnit.MINUTES);
                return BaseResponse.success(vipPrices);
            }else {
                return BaseResponse.error("失败");
            }
        }else {
            return BaseResponse.success(vipPrices);
        }
    }
}
