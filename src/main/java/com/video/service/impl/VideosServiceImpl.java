package com.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.AllCollectionsDto;
import com.video.mapper.HistoryMapper;
import com.video.mapper.TypeMapper;
import com.video.mapper.VideoMapper;
import com.video.mapper.VideosAddressMapper;
import com.video.pojo.*;
import com.video.service.VideosAddressService;
import com.video.utils.*;
import com.video.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: VideosServiceImpl
 * @author: 赵容庆
 * @date: 2022年09月23日 16:58
 * @Description: TODO
 */
@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class VideosServiceImpl implements VideosAddressService {
    @Autowired
    private VideosAddressMapper videosAddressMapper;
    @Autowired
    private CosUtil cosUtil;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private HistoryMapper historyMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private TypeMapper typeMapper;

    @Override
    public BaseResponse addVideosAddress(VideoVo videoVo) {
        Integer videoId = videoVo.getVideoId();
        List<String> collections= videoVo.getCollections();
        MultipartFile[] files = videoVo.getFiles();
        List<String> videoTypes = videoVo.getVideoTypes();
        List<Integer> vips = videoVo.getVips();
        List<String> sidelightsName = videoVo.getSidelightName();
        if (videoId == null || collections.isEmpty() || files == null
                || videoTypes.isEmpty() || vips.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
        for (Integer a : vips) {
            if (a == 1) {
                Videos videos = new Videos();
                videos.setId(videoId);
                videos.setVip(1);
                videoMapper.updateById(videos);
                break;
            }
        }

        int j = 0;
        for (int i = 0; i< collections.size(); i++) {
            VideosAddress videosAddress = new VideosAddress();
            videosAddress.setVideoId(videoId);
            videosAddress.setCollection(collections.get(i));
            if (!FileTypeUtil.isMp4(files[i]) && !FileTypeUtil.isMp4_1(files[i])) {
                return BaseResponse.error("视频格式错误");
            }
            if (sidelightsName != null && !sidelightsName.isEmpty()) {
                videosAddress.setSidelightsName(sidelightsName.get(i));
            }
            String address = cosUtil.upload(files[i],1);
            videosAddress.setAddress(address);
            videosAddress.setVideoType(videoTypes.get(i));
            videosAddress.setVip(vips.get(i));
            j += videosAddressMapper.insert(videosAddress);
        }
        return j == collections.size() ? BaseResponse.success("上传成功") : BaseResponse.error("上传失败");
    }

    @Override
    public BaseResponse getAddress(Integer videoId, Integer videoAddressId) {
        Integer id = null;
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            id = loginUser.getUser().getId();
        }catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (videoAddressId == null || videoAddressId == 0) {
            LambdaQueryWrapper<VideosAddress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(VideosAddress::getVideoId,videoId).orderByAsc(VideosAddress::getCollection).last("limit 1");
            try {
                videoAddressId = videosAddressMapper.selectOne(wrapper).getId();
            }catch (NullPointerException e) {
               return BaseResponse.error("无数据");
            }


        } else {
            //获取视频id
            VideosAddress videosAddress = videosAddressMapper.selectById(videoAddressId);
            videoId = videosAddress.getVideoId();
        }
        //获取视频的播放次数
        Videos videos = videoMapper.selectById(videoId);
        String[] types = videos.getTypeName().split("，");
        for(String type : types) {
            LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Type::getTypeName,type);
            Integer id1 = typeMapper.selectOne(wrapper).getId();
            redisCache.deleteObject("LookedTop:"+id1);
            //将视频id与次数存入redis
            redisCache.setCacheZSet("sift",videoId,1);
            if (redisCache.getCacheZSetScore("sift",videoId) != null) {
                redisCache.setCacheZSet("sift",videoId,redisCache.getCacheZSetScore("sift",videoId)+1);
                redisCache.expire("sift",24, TimeUnit.HOURS);
            }
            if (redisCache.getCacheZSetScore("VideoLookNums:" + type, videoId) == null || redisCache.getCacheZSetScore("VideoLookNums:" + type, videoId) == 0) {
                redisCache.setCacheZSet("VideoLookNums:" + type, videoId,  1);
                redisCache.setCacheZSet("sift",videoId,1);
            } else {
                redisCache.setCacheZSet("VideoLookNums:" + type, videoId, redisCache.getCacheZSetScore("VideoLookNums:" + type, videoId) + 1);
            }
        }
        if (id == null) {
            return videosAddressMapper.selectById(videoAddressId) != null ? BaseResponse.success(videosAddressMapper.selectById(videoAddressId)) : BaseResponse.success("无数据");
        } else {
            LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(History::getVideoAddressId,videoAddressId).eq(History::getUserId,id);
            History history = historyMapper.selectOne(wrapper);
            if(!Objects.isNull(history)) {
                return BaseResponse.success(historyMapper.getOneHistory(history.getId()));
            }
            return videosAddressMapper.selectById(videoAddressId) != null ? BaseResponse.success(videosAddressMapper.selectById(videoAddressId)) : BaseResponse.success("无数据");
        }

    }

    @Override
    public BaseResponse deleteVideoAddress(List<Integer> ids) {
        if (ids.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
        List<VideosAddress> videosAddresses = videosAddressMapper.selectBatchIds(ids);
        for (VideosAddress a : videosAddresses) {
            String address = a.getAddress();
            cosUtil.deleteObject(address,1);
        }
        return videosAddressMapper.deleteBatchIds(ids) > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse updateVip(List<Integer> ids,List<Integer> vips) {
        if (ids.isEmpty()){
            return BaseResponse.nullValue("数据不能为空");
        }
        int count = 0;
        for (int i = 0; i < ids.size(); i++) {
            VideosAddress videosAddress = new VideosAddress();
            videosAddress.setVip(vips.get(i));
            videosAddress.setId(ids.get(i));
            count += videosAddressMapper.updateById(videosAddress);
        }
        return count == ids.size() ? BaseResponse.success("已修改") : BaseResponse.error("数据错误");
    }

    @Override
    public BaseResponse updateStatus(Integer id, Integer status) {
        if(id == null || status == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        VideosAddress videosAddress = new VideosAddress();
        videosAddress.setId(id);
        videosAddress.setVideoStatus(status);
        return videosAddressMapper.updateById(videosAddress) > 0 ? BaseResponse.success("已修改") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getAddressId(Integer nodePage, Integer pageSize, Integer videoId, Integer status) {
        if (nodePage <= 0 || pageSize <= 0) {
            return BaseResponse.error("页数不能小于0");
        }
        if(videoId == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        //分页实现
        Page<AllCollectionsDto> page = new Page<>(nodePage,pageSize);
        Page<AllCollectionsDto> page1 = videosAddressMapper.selectAddressId(videoId,page,status);
        return page1 != null ? BaseResponse.success(page1) : BaseResponse.error("无数据");
    }

    @Override
    public BaseResponse getHighlights(Integer videoId,Integer nodePage, Integer pageSize) {
        if(videoId == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Page<VideosAddress> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<VideosAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideosAddress::getVideoId,videoId)
                .eq(VideosAddress::getVideoType,0);
        Page<VideosAddress> page1 = videosAddressMapper.selectPage(page, wrapper);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getAddressId2(Integer videoId, Integer status) {
        if(videoId == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        //分页实现
        List<AllCollectionsDto> list = videosAddressMapper.selectAddressId2(videoId,status);
        return !list.isEmpty() ? BaseResponse.success(list) : BaseResponse.error("无数据");
    }

    @Override
    public BaseResponse getsidelights(Integer id) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        LambdaQueryWrapper<VideosAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideosAddress::getVideoId,id)
                .eq(VideosAddress::getVideoType,0)
                .eq(VideosAddress::getVideoStatus,1);
        List<VideosAddress> videosAddresses = videosAddressMapper.selectList(wrapper);
        return videosAddresses.isEmpty() ? BaseResponse.success("没有数据") : BaseResponse.success(videosAddresses);
    }

    @Override
    public BaseResponse getAddress2(Integer id) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        VideosAddress videosAddress = videosAddressMapper.selectById(id);
        return Objects.isNull(videosAddress) ? BaseResponse.success("没有数据") : BaseResponse.success(videosAddress);
    }
}
