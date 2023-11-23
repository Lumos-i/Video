package com.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.video.mapper.CollectionsMapper;
import com.video.mapper.VideoMapper;
import com.video.pojo.*;
import com.video.service.CollectionsService;
import com.video.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author zrq
 * @ClassName CollectiosServiceImpl
 * @date 2022/10/5 9:52
 * @Description TODO
 */
@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class CollectiosServiceImpl implements CollectionsService {
    @Autowired
    private CollectionsMapper collectionsMapper;
    @Autowired
    private VideoMapper videoMapper;


    @Override
    public BaseResponse collectVideo(Integer videoId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        if(videoId == null || id == null) {
            return  BaseResponse.nullValue("数据不能为空");
        }
        Videos videos1 = videoMapper.selectById(videoId);
        //获得收藏量
        Integer collectionNums =videos1.getCollectionNums();
//        String videoName = videos1.getVideoName();
//        Integer userId = videos1.getUserId();
        Videos videos = new Videos();
        int insert = 0;
        //查询条件
        LambdaQueryWrapper<Collections> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collections::getUserId,id).eq(Collections::getVideoId,videoId);
        //如果不存在
        if( Objects.isNull(collectionsMapper.selectOne(wrapper))){
            insert = collectionsMapper.insert(new Collections(null, id, videoId));
            videos.setId(videoId);
            videos.setCollectionNums(collectionNums+1);
            videoMapper.updateById(videos);
//            String content = "您的视频"+videoName+"已被收藏";
//            Remind remind = new Remind(null,userId,content,null,id,1,0);
//            remindMapper.insert(remind);
        }else {
            videos.setCollectionNums(collectionNums-1);
            videoMapper.updateById(videos);
            collectionsMapper.delete(wrapper);
        }

        return insert > 0 ? BaseResponse.success("已添加收藏") : BaseResponse.error("已删除");
    }

    @Override
    public BaseResponse deleteCollections(List<Integer> ids) {
        if (ids.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
        int i = collectionsMapper.deleteBatchIds(ids);
        return i > 0 ? BaseResponse.success("已删除") : BaseResponse.error("已经没有数据了");
    }

    @Override
    public BaseResponse haveCollected(Integer id, Integer videoId) {
        if(id == null || videoId == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        LambdaQueryWrapper<Collections> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collections::getUserId,id).eq(Collections::getVideoId,videoId);

        return Objects.isNull(collectionsMapper.selectOne(wrapper)) ? BaseResponse.success("未收藏") : BaseResponse.success("已收藏");
    }



}
