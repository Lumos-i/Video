package com.video.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.mapper.AdvertisingMapper;
import com.video.pojo.Advertising;
import com.video.pojo.LoginUser;
import com.video.service.AdvertisingService;
import com.video.utils.BaseResponse;
import com.video.utils.CosUtil;
import com.video.utils.FileTypeUtil;

import com.video.utils.WeightRandomUtil;
import com.video.vo.AdvertisingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zrq
 * @ClassName AdvertisingServiceImpl
 * @date 2022/10/6 9:48
 * @Description TODO
 */
@Service
@Transactional
@SuppressWarnings("rawtypes")
public class AdvertisingServiceImpl implements AdvertisingService {
    @Autowired
    private AdvertisingMapper advertisingMapper;
    @Autowired
    private CosUtil cosUtil;

    @Override
    public BaseResponse addAdvertising(AdvertisingVo advertisingVo) {
        MultipartFile address = advertisingVo.getAddress();
        MultipartFile photo = advertisingVo.getCover();

        if (!FileTypeUtil.isImg(photo)) {
            return BaseResponse.error("图片格式不正确");
        }
        if (!FileTypeUtil.isMp4(address) && !FileTypeUtil.isMp4_1(address)) {
            return BaseResponse.error("视频格式不正确");
        }
        LoginUser loginUser = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        String uploadAddress = cosUtil.upload(address, 1);
        String uploadPhoto = cosUtil.upload(photo, 0);
        Advertising advertising = new Advertising(null, uploadPhoto, uploadAddress, id,advertisingVo.getWeight());
        return advertisingMapper.insert(advertising) > 0 ? BaseResponse.success("上传成功") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse deleteAdvertising(List<Integer> ids) {
        if (ids.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
       return advertisingMapper.deleteBatchIds(ids) > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getAllAdvertising(Integer nodePage, Integer pageSize) {
        Page<Advertising> page = new Page<>(nodePage, pageSize);
        Page<Advertising> page1 = advertisingMapper.selectPage(page, null);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getSomeAdvertising(Integer num) {
        if(num == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
//        List<Advertising> someAdvertising = advertisingMapper.getSomeAdvertising(num);
        List<Advertising> advertisings = advertisingMapper.selectList(null);
        WeightRandomUtil weightRandomUtil = new WeightRandomUtil(advertisings);
        List<Advertising> res = new ArrayList<>();
        Integer integer = weightRandomUtil.nextItem();
        for (int i = 0; i < num; i++) {
            res.add(advertisingMapper.selectById(integer));
        }
//        Random rand = new Random();
//        List<Advertising> res = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            int randNumber = rand.nextInt(advertisings.size()-1 - 0 + 1);
//            res.add(advertisings.get(randNumber));
//        }

        return res.isEmpty() ? BaseResponse.success("无数据")
                : BaseResponse.success(res);
    }
}
