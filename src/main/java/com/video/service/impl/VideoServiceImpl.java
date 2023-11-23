package com.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.*;
import com.video.mapper.*;
import com.video.pojo.*;
import com.video.pojo.Collections;
import com.video.service.VideoService;
import com.video.utils.*;
import com.video.vo.VideoInfoVo;
import com.video.vo.VideoInfoVoTwo;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoServiceImpl
 * @author: 赵容庆
 * @date: 2022年09月23日 10:29
 * @Description: TODO
 */

@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private TypeMapper typeMapper;
    @Autowired
    private VideosAddressMapper videosAddressMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CosUtil cosUtil;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentLoveMapper commentLoveMapper;
    @Autowired
    private CollectionsMapper collectionsMapper;
    @Autowired
    private HistoryMapper historyMapper;
    @Resource
    private DFAUtil dfaUtil;

    /**
     * 添加视频
     * @param videoName
     * @param covers
     * @param fragments
     * @param introduction
     * @param actors
     * @param director
     * @param typeName
     * @param allCollections
     * @return
     */
    @Override
    public BaseResponse addVideoInfo( String videoName, MultipartFile covers, MultipartFile fragments, String introduction,
                                     String actors, String director, String typeName, String allCollections) {
        if (StringUtils.isEmpty(videoName.trim()) || StringUtils.isEmpty(introduction.trim())
                || StringUtils.isEmpty(actors.trim()) || typeName == null|| StringUtils.isEmpty(allCollections)) {
            return BaseResponse.nullValue("数据不能为空");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        String cover = "";
        String fragment = "";
        //todo 判断上传视频的权限
        User user = userMapper.selectById(userId);
        Integer uploadPower = user.getUploadPower();
        if (uploadPower == 0) {
            return BaseResponse.noPower("请申请成为视频发布者后再发布视频");
        }
        //判断文件类型
        if(FileTypeUtil.isImg(covers) || FileTypeUtil.isMp4(fragments) || FileTypeUtil.isMp4_1(fragments)) {
            cover = cosUtil.upload(covers, 0);
            fragment = cosUtil.upload(fragments, 1);
        }else {
            return BaseResponse.errWeb("文件类型错误");
        }
        String[] split = typeName.split("，");
        for (String a : split) {
           LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
           wrapper.eq(Type::getTypeName,a);
           if (Objects.isNull(typeMapper.selectOne(wrapper))) {
               return BaseResponse.nullValue("所填类型不存在");
           }
        }
        Videos videos = new Videos(userId, videoName,director,cover,fragment,introduction,actors,typeName,allCollections);
        int insert = videoMapper.insert(videos);
        LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Videos::getVideoName,videoName).eq(Videos::getUserId,userId);
        Videos videos1 = videoMapper.selectOne(wrapper);
        return insert > 0 ? BaseResponse.success("上传成功",videos1.getId()) : BaseResponse.error("服务器错误");
    }

    /**
     * 取出视频信息
     * @param id
     * @return
     */
    @Override
    public BaseResponse getVideoInfo(Integer id) {
        if(id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Videos videos = videoMapper.selectById(id);
        return videos != null ? BaseResponse.success(videos) : BaseResponse.error("服务器错误");
    }

    /**
     * 获得首页展示的视频
     * @param typeName
     * @return
     */
    @Override
    public BaseResponse getVideos(String typeName,Integer nodePage, Integer pageSize) {
        if (typeName == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Page<ShowVideoDto> page = new Page<>(nodePage,pageSize);
        Page<ShowVideoDto> page1 = videoMapper.showVideos(typeName,page);
        return page1.getSize() ==0 ? BaseResponse.success("数据为空") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse deleteVideo(List<Integer> ids) {
        if (ids.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
        List<VideosAddress> videosAddresses = null;
        for (int j = 0; j < ids.size(); j++) {
            Videos videos = videoMapper.selectById(ids.get(j));
            String cover = videos.getCover();
            String fragment = videos.getFragment();
            //删除排行榜.
            redisCache.deleteObject("LookedTop:"+ids.get(j));
            String[] split = videos.getTypeName().split("，");
            for (String type : split) {
               redisCache.deleteCacheZSet("VideoLookNums:"+type,ids.get(j));

            }
            cosUtil.deleteObject(fragment,1);
            cosUtil.deleteObject(cover,2);
            LambdaQueryWrapper<VideosAddress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(VideosAddress::getVideoId, ids.get(j));
            videosAddresses = videosAddressMapper.selectList(wrapper);
            historyMapper.delete(new LambdaQueryWrapper<History>().in(History::getVideoId,ids));
            collectionsMapper.delete(new LambdaQueryWrapper<Collections>().in(Collections::getVideoId,ids));
        }
        if (videosAddresses != null && !videosAddresses.isEmpty() && videosAddresses.size() != 0) {
            for (VideosAddress videosAddress : videosAddresses) {
                String address = videosAddress.getAddress();
                cosUtil.deleteObject(address, 1);
            }
        }

        int i = videoMapper.deleteBatchIds(ids);

        videosAddressMapper.deleteBatchVideoIds(ids);
        return i > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse updateVideoInfo(VideoInfoVoTwo videoInfoVoTwo) {
        String videoName = videoInfoVoTwo.getVideoName();
        String director = videoInfoVoTwo.getDirector();
        String introduction = videoInfoVoTwo.getIntroduction();
        String actors = videoInfoVoTwo.getActors();
        Integer id = videoInfoVoTwo.getId();
        MultipartFile cover = videoInfoVoTwo.getCover();
        MultipartFile fragment = videoInfoVoTwo.getFragment();
        String typeName = videoInfoVoTwo.getTypeName();
        String allCollections = videoInfoVoTwo.getAllCollections();
        Videos videos = videoMapper.selectById(id);
        String oldCover = null;
        String newCover = null;
        String oldFragment = null;
        String newFragment = null;
        if (cover != null) {
            oldCover = videos.getCover();
            cosUtil.deleteObject(oldCover,0);
            newCover = cosUtil.upload(cover, 0);
        }
        if (fragment != null) {
            oldFragment = videos.getFragment();
            cosUtil.deleteObject(oldFragment, 1);
            newFragment = cosUtil.upload(fragment, 1);
        }
        if (!StringUtils.isEmpty(typeName)) {
            String[] split = typeName.split("，");
            for (String a : split) {
                LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Type::getTypeName, a);
                if (Objects.isNull(typeMapper.selectOne(wrapper))) {
                    return BaseResponse.nullValue("所填类型不存在");
                }
            }
        }

        int a = videoMapper.updateVideo(id,videoName,director,introduction,actors,typeName,allCollections,newCover,newFragment);
        return a > 0 ? BaseResponse.success("已修改") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse lookVideo(String text, Integer nodePage, Integer pageSize) {
        if (StringUtils.isEmpty(text.trim())) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Integer id =null;
        Page<LookResultDto> page = new Page<>(nodePage,pageSize);
        Page<LookResultDto> lookResultDtoPage = videoMapper.lookVideo(page, text);
        //添加到redis
        if (redisCache.getCacheZSetScore("LookTop",text) == null || redisCache.getCacheZSetScore("LookTop",text) == 0) {
            redisCache.setCacheZSet("LookTop", text, 1);
        }else {
            redisCache.setCacheZSet("LookTop", text, redisCache.getCacheZSetScore("LookTop", text)+1);
        }

        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            id = loginUser.getUser().getId();
        }catch (ClassCastException e) {
            e.printStackTrace();
        }
        log.info(id+"=================");
        //添加搜索记录
        if (id != null) {
            redisCache.setCacheList("LookHistory" + id,Arrays.asList(text));
        }
        return page.getSize() == 0 ? BaseResponse.nullValue("无数据") : BaseResponse.success(lookResultDtoPage);
    }

    @Override
    public BaseResponse getTop(Integer id) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        List<TopDto> top = videoMapper.getTop(id);
        return !top.isEmpty() ? BaseResponse.success(top) : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getLookTop() {
        Set lookTop = redisCache.getCacheZSetLookTop("LookTop",10);
        return lookTop.isEmpty() ? BaseResponse.success("无数据") : BaseResponse.success(lookTop);
    }

    @Override
    public BaseResponse allTop(Integer typeId, Integer num) {
        if (typeId == null) {
            return BaseResponse.nullValue("数据为空");
        }
        List<Videos> videos = null;
//        List<Videos> res = new ArrayList();
        if (redisCache.getCacheList("LookedTop:"+typeId).isEmpty() || redisCache.getCacheList("LookedTop:"+typeId).size() == 0) {
            Type types = typeMapper.selectById(typeId);
            String type = types.getTypeName();
            Set<Integer> cacheZSetLookTop = (Set<Integer>) redisCache.getCacheZSetLookTop("VideoLookNums:" + type, num);
            log.info("排行榜数据："+cacheZSetLookTop);
            videos = new ArrayList<>();
            for (Integer a : cacheZSetLookTop) {
                if (Objects.isNull(videoMapper.selectById(a))) {
                    continue;
                }
                videos.add(videoMapper.selectById(a));
            }
            try {
                redisCache.setCacheList("LookedTop:" + typeId, videos);
            }catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }else {
            videos = redisCache.getCacheList("LookedTop:"+typeId);
        }
        redisCache.expire("LookedTop:"+typeId,60 * 24, TimeUnit.MINUTES);
        return videos.size() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(videos);
    }

    @Override
    public BaseResponse slideshow(Integer videoId, Integer slideshow) {
        if (videoId == null || slideshow == null) {
            return  BaseResponse.nullValue("数据不能为空");
        }
        LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Videos::getSlideshow,1);
        Integer integer = videoMapper.selectCount(wrapper);
        if (integer > 20 && slideshow == 1) {
            return BaseResponse.success("轮播图已达到最大数量");
        }
        return videoMapper.updateById(new Videos(videoId,slideshow)) > 0 ? BaseResponse.success("修改成功") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getSlideshow(Integer nodePage, Integer pageSize) {
        Page<Videos> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Videos::getSlideshow,1);
        Page<Videos> page1 = videoMapper.selectPage(page, wrapper);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse addComment(Integer videoId,String comment) {
        if(videoId<=0 || comment.isEmpty()){
            return BaseResponse.error("数据错误");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        Comment com = new Comment();
        com.setUserId(userId);
        com.setVideoId(videoId);
        com.setCommentLevel(1);
        HashMap<Boolean, String> map = dfaUtil.getSensitiveWordByDFAMap(comment, 2);
        if(map.containsKey(true)){
            com.setComment(map.get(true));
        }
        com.setComment(comment);
        com.setCommentTime(LocalDateTime.now());
//        //添加提醒
//        Videos videos = videoMapper.selectById(videoId);
//        Integer userId1 = videos.getUserId();
//        String videoName = videos.getVideoName();
//        String content = "有人您的视频"+videoName+"下评论了";
//        Remind remind = new Remind(null,userId1,content,null,videoId,2,0);
//        remindMapper.insert(remind);
        return commentMapper.insert(com)>0?BaseResponse.success(com):BaseResponse.error("添加评论失败");
    }

    @Override
    public BaseResponse addCommentReply(Integer videoId, String comment, Integer commentId) {
        if(videoId<=0 || comment.isEmpty() || commentId<0){
            return BaseResponse.error("数据错误");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        Comment com = new Comment();
        com.setUserId(userId);
        com.setVideoId(videoId);
        com.setCommentLevel(2);
        com.setCommentId(commentId);
        HashMap<Boolean, String> map = dfaUtil.getSensitiveWordByDFAMap(comment, 2);
        if(map.containsKey(true)){
            com.setComment(map.get(true));
        }
        com.setComment(comment);
        com.setCommentTime(LocalDateTime.now());
//        //添加提醒
//        Comment comment1 = null;
//        Integer userId1 = null;
//        String content = "";
//        Integer commentId1 = null;
//        if (commentId != 0) {
//           comment1 = commentMapper.selectById(commentId);
//           userId1 = comment1.getUserId();
//           commentId1 = comment1.getCommentId();
//            content = "您有评论被回复了快看看吧";
//        }
//        Remind remind = new Remind(null,userId1,content,null,commentId1,2,0);
//        remindMapper.insert(remind);
        return commentMapper.insert(com)>0?BaseResponse.success(com):BaseResponse.error("添加评论失败");
    }

    @Override
    public BaseResponse commentLove(Integer videoId, Integer commentId) {
        if(videoId<=0 || commentId<=0){
            return BaseResponse.error("数据错误");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        LambdaQueryWrapper<CommentLove> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLove::getUserId,userId)
                .eq(CommentLove::getCommentId,commentId)
                .eq(CommentLove::getVideoId,videoId);
        CommentLove love = commentLoveMapper.selectOne(queryWrapper);
        LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Comment::getId,commentId);
        int result;
        if(love==null){
            CommentLove commentLove = new CommentLove();
            commentLove.setUserId(userId);
            commentLove.setVideoId(videoId);
            commentLove.setCommentId(commentId);
            result = commentLoveMapper.insert(commentLove);
            updateWrapper.setSql("comment_love = comment_love + 1");
            result = result + commentMapper.update(null,updateWrapper);
//            //添加提醒
//            Comment comment1 = null;
//            Integer userId1 = null;
//            String content = "";
//            Integer commentId1 = null;
//            if (commentId != 0) {
//                comment1 = commentMapper.selectById(commentId);
//                userId1 = comment1.getUserId();
//                commentId1 = comment1.getCommentId();
//                content = "您有评论被点赞了";
//            }
//            Remind remind = new Remind(null,userId1,content,null,commentId1,2,0);
//            remindMapper.insert(remind);
            return result==2?BaseResponse.success("点赞成功！"):BaseResponse.error("点赞失败！");
        }else {
            result = commentLoveMapper.delete(queryWrapper);
            updateWrapper.setSql("comment_love = comment_love - 1");
            result = result + commentMapper.update(null,updateWrapper);
            return result==2?BaseResponse.success("取消点赞成功！"):BaseResponse.error("取消点赞失败！");
        }
    }

    @Override
    public BaseResponse getCommentPage(Integer videoId, Integer nodePage, Integer pageSize) {
        if(videoId<=0){
            return BaseResponse.error("数据不正确");
        }
        if(nodePage<=0){
            nodePage=1;
        }
        if(pageSize<=0){
            pageSize=10;
        }
        try {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getVideoId, videoId)
                    .eq(Comment::getCommentLevel, 1);
            Page<Comment> page = new Page<>(nodePage, pageSize);
            commentMapper.selectPage(page, queryWrapper);

            Page<CommentDto> dtoPage=new Page<>();
            BeanUtil.copyProperties(page,dtoPage,"records");
            List<Comment> pageRecords = page.getRecords();
            List<CommentDto> dtos = pageRecords.stream().map((item) -> {
                CommentDto commentDto = new CommentDto();
                BeanUtil.copyProperties(item, commentDto);
                LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Comment::getCommentId, commentDto.getId())
                        .eq(Comment::getCommentLevel, 2);
                commentDto.setCommentReplyNumber(commentMapper.selectCount(wrapper));
                return commentDto;
            }).collect(Collectors.toList());
            dtoPage.setRecords(dtos);
            if (dtoPage.getRecords().size()!=0) {
                return BaseResponse.success("成功", dtoPage);
            }else {
                return BaseResponse.success("暂无评论");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return BaseResponse.error("查询出现错误！");
    }

    @Override
    public BaseResponse getCommentReplyPage(Integer commentId, Integer nodePage, Integer pageSize) {
        if(nodePage <= 0){
            nodePage = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        try {
            Page<CommentReplyDto> page=new Page<>(nodePage,pageSize);
            commentMapper.getCommentReplyPag(page,commentId);
            if (page.getRecords().size()!=0) {
                return BaseResponse.success("成功", page);
            }
            return BaseResponse.success("暂无回复");
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.error("查询出现错误！");
        }
    }

    @Override
    public BaseResponse getUserLoveCommentList(Integer videoId) {
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer userId = loginUser.getUser().getId();
            LambdaQueryWrapper<CommentLove> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CommentLove::getUserId, userId)
                    .eq(CommentLove::getVideoId,videoId);
            List<CommentLove> commentLoves = commentLoveMapper.selectList(queryWrapper);
            if (!commentLoves.isEmpty()) {
                return BaseResponse.success("成功！", commentLoves);
            }
            return BaseResponse.success("该用户暂无点赞在该视频下的评论");
        }catch (Exception e){
            return BaseResponse.error("查询出现错误！");
        }
    }

    @Override
    public BaseResponse getVideoByClassify(String classify, Integer nodePage, Integer pageSize) {
        LambdaQueryWrapper<Videos> wrapper = null;
        String[] split = classify.split("，");
        String newClassify = "";
        if (split[split.length-1].equals("1") || split[split.length-1] == "1") {
            log.info("进入vip判定");
            for (int i = 0; i < split.length-1; i++) {
                newClassify += split[i]+"，";
            }
            newClassify = newClassify.substring(0,newClassify.length()-1);
            log.info("字符："+newClassify);
            Page<Videos> page = new Page<>(nodePage, pageSize);
            wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Videos::getTypeName,newClassify).eq(Videos::getVip,1);
            Page<Videos> page1 = videoMapper.selectPage(page, wrapper);
            return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
        }
        log.info("未进入vip判定");
        Page<Videos> page = new Page<>(nodePage, pageSize);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Videos::getTypeName,classify).eq(Videos::getVip,0);
        Page<Videos> page1 = videoMapper.selectPage(page, wrapper);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getAuditVideo(Integer nodePage, Integer pageSize,Integer status) {
        if(nodePage==null||pageSize==null){
            return BaseResponse.nullValue("数据为空");
        }
        if(nodePage <= 0){
            nodePage=1;
        }
        if(pageSize <= 0){
            pageSize=10;
        }
        if(status<0 || status>2){
            status=null;
        }
        try {
            Page<AuditVideosDto> page = new Page<>(nodePage, pageSize);
            videosAddressMapper.getAuditVideos(page,status);
            if (page.getTotal() != 0) {
                return BaseResponse.success(page);
            }
            return BaseResponse.success("无待审核视频");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return BaseResponse.error("出现错误");
        }
    }

    @Override
    public BaseResponse updateVideoStatus(Integer id, Integer status) {
        if(id==null || status==null){
            return BaseResponse.nullValue("数据为空");
        }
        if(id<=0 || status<1 || status>2){
            return BaseResponse.error("数据不正确");
        }
        LambdaUpdateWrapper<VideosAddress> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(VideosAddress::getId,id)
                        .set(VideosAddress::getVideoStatus,status);
        Integer result = videosAddressMapper.update(null,updateWrapper);
        if(result==1){
            return BaseResponse.success("成功");
        }else {
            return BaseResponse.error("失败");
        }
    }

    @Override
    public BaseResponse lookHistory() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        List<Objects> cacheList = redisCache.getCacheList("LookHistory" + id);
        java.util.Collections.reverse(cacheList);
        return cacheList.isEmpty() ? BaseResponse.success("无数据") : BaseResponse.success(cacheList);
    }



    @Override
    public BaseResponse getAllVideos(Integer nodePage, Integer pageSize) {
        if(nodePage==null||pageSize==null){
            return BaseResponse.nullValue("数据为空");
        }
        if(nodePage <= 0){
            nodePage=1;
        }
        if(pageSize <= 0){
            pageSize=10;
        }
        return null;
    }

    @Override
    public BaseResponse updateVideoAddress(Integer id, String collections, MultipartFile file, String sidelightsName, String videoType, Integer vip) {
        if (id ==null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        String newAddress = null;
        if (file != null) {
            if (!FileTypeUtil.isMp4_1(file) && !FileTypeUtil.isMp4(file)) {
                return BaseResponse.error("文件格式错误");
            }
            newAddress = cosUtil.upload(file, 1);
            String address = videosAddressMapper.selectById(id).getAddress();
            cosUtil.deleteObject(address, 1);
        }
        int i = videosAddressMapper.updateVideoAddress(id,collections,newAddress,sidelightsName,videoType,vip);
        return i > 0 ? BaseResponse.success("已修改") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getCommentLoveNumber(Integer commentId) {
        if(commentId==null || commentId<=0){
            return BaseResponse.error("数据错误");
        }
        Comment comment = commentMapper.selectById(commentId);
        if(comment!=null){
            return BaseResponse.success(comment.getCommentLove());
        }
        return BaseResponse.error("没有这条评论");
    }

    @Override
    public BaseResponse isCommentLove(Integer commentId) {
        Integer userId;
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userId = loginUser.getUser().getId();
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.error("请先登录");
        }
        if(commentId==null || commentId<=0){
            return BaseResponse.error("数据不正确");
        }
        LambdaQueryWrapper<CommentLove> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLove::getUserId,userId)
                .eq(CommentLove::getCommentId,commentId);
        CommentLove commentLove = commentLoveMapper.selectOne(queryWrapper);
        if(commentLove!=null){
            return BaseResponse.success("已点赞");
        }else {
            return BaseResponse.success("未点赞");
        }
    }

    @Override
    public BaseResponse getSiftVideo() {
        Set<Integer> sift =(Set<Integer>) redisCache.getCacheZSetLookTop("sift", 8);
        List<Videos> list = new ArrayList<>();

        for (Integer id : sift) {
            Videos videos = videoMapper.selectById(id);
            if (videos == null) {
                continue;
            }
            LambdaQueryWrapper<VideosAddress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(VideosAddress::getVideoId,id)
                    .eq(VideosAddress::getVideoStatus,1);
            if (videosAddressMapper.selectList(wrapper).isEmpty()) {
                continue;
            }
            list.add(videoMapper.selectById(id));

        }
        return list.isEmpty() ? BaseResponse.success("无数据") : BaseResponse.success(list);
    }

    @Override
    public BaseResponse lookUserPage(Integer uploadPower,String text,Integer nodePage, Integer pageSize) {
        if(nodePage==null||nodePage<=0){
            nodePage=1;
        }
        if(pageSize==null||pageSize<=0){
            pageSize=10;
        }
        if(uploadPower!=null&&uploadPower!=1){
            uploadPower=null;
        }
        Page<User> page=new Page<>(nodePage,pageSize);
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(text),User::getName,text)
                .eq(uploadPower!=null,User::getUploadPower,1);
        userMapper.selectPage(page,queryWrapper);
        Integer id = null;
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            id = loginUser.getUser().getId();
        }catch (Exception e){
            System.out.println("未登录的搜索");
        }
        //添加搜索记录
        if (id != null) {
            redisCache.setCacheZSet("LookHistory" + id, text,1);
        }
        return page.getTotal()!=0?BaseResponse.success(page):BaseResponse.success("没有相应的数据");
    }

    @Override
    public BaseResponse getUserVideo(Integer id, Integer status, Integer nodePage, Integer pageSize) {
        if (status == null ) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Page<VideoDetailDto> page = new Page<>(nodePage, pageSize);
        Page<VideoDetailDto> page1 = userMapper.getMyVideo(page, id, status);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse deleteUserLookHistory(String text) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        redisCache.removeList("LookHistory" + id,1,text);
        return BaseResponse.success("已删除");
    }
    @Override
    public BaseResponse deleteUserLookHistory() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        redisCache.deleteObject("LookHistory" + id);
        return BaseResponse.success("已删除");
    }
}