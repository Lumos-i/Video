package com.video.controller;

import com.video.limit.AccessLimit;
import com.video.service.CollectionsService;
import com.video.service.VideoService;
import com.video.service.VideosAddressService;
import com.video.utils.BaseResponse;
import com.video.vo.VideoInfoVo;
import com.video.vo.VideoInfoVoTwo;
import com.video.vo.VideoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @ClassName: VideoController
 * @author: 赵容庆
 * @date: 2022年09月23日 10:28
 * @Description: 视频接口
 */

@RestController
@RequestMapping("/video")
@Api(tags = {("视频接口")})
@SuppressWarnings("rawtypes")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideosAddressService videosAddressService;
    @Autowired
    private CollectionsService collectionsService;

    @PostMapping("/addVideoInfo")
    @ApiOperation("添加视频信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoName", value = "视频名"),
            @ApiImplicitParam(name = "cover", value = "封面"),
            @ApiImplicitParam(name = "fragment", value = "精彩片段"),
            @ApiImplicitParam(name = "introduction", value = "视频介绍"),
            @ApiImplicitParam(name = "actors", value = "演员"),
            @ApiImplicitParam(name = "director", value = "演员"),
            @ApiImplicitParam(name = "typeName", value = "类型名"),
            @ApiImplicitParam(name = "allCollections", value = "总集数")

    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse addVideoInfo(String videoName,MultipartFile cover,MultipartFile fragment, String introduction,
                                     String actors,String director, String typeName, String allCollections) {
        return videoService.addVideoInfo(videoName,cover,fragment,introduction,actors, director,typeName, allCollections);
    }

    @PostMapping("/addVideo")
    @ApiOperation("添加视频")

    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse addVideo(VideoVo videoVo) {

        return videosAddressService.addVideosAddress(videoVo);
    }
    @GetMapping("/logged/getAddressId")
    @ApiOperation("取出某一视频所有视频地址(有分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频的id"),
            @ApiImplicitParam(name = "status", value = "0 未审核 1 审核通过 2 审核未通过 ")
    })
    public BaseResponse getAddress(@RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                   @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize,
                                   Integer videoId,
                                   Integer status) {
        return videosAddressService.getAddressId(nodePage, pageSize, videoId, status);
    }

    @GetMapping("/logged/getAddressId2")
    @ApiOperation("取出某一视频所有视频地址(无分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频的id"),
            @ApiImplicitParam(name = "status", value = "0 未审核 1 审核通过 2 审核未通过 ")
    })
    public BaseResponse getAddress2(Integer videoId, Integer status) {
        return videosAddressService.getAddressId2(videoId,status);
    }

    @GetMapping("/logged/getVideoInfo")
    @ApiOperation("取出某一视频信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "视频的id"),
    })
    public BaseResponse getVideoInfo(Integer id) {
        return videoService.getVideoInfo(id);
    }



    @GetMapping("/logged/showVideos")
    @ApiOperation("首页展示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeName", value = "类别名")
    })
    public BaseResponse getVideos(String typeName, @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                  @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return videoService.getVideos(typeName, nodePage, pageSize);
    }

    @PutMapping("/updateVideoInfo")
    @ApiOperation("修改视频信息")

    public BaseResponse updateVideoInfo(VideoInfoVoTwo videoInfoVoTwo) {
        return videoService.updateVideoInfo(videoInfoVoTwo);
    }




    @GetMapping("/logged/look")
    @ApiOperation("搜索视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "text", value = "搜索内容")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse lookVideo(String text,@RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                  @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return videoService.lookVideo(text, nodePage, pageSize);
    }
    @GetMapping("/logged/getVideoByClassify")
    @ApiOperation("根据分类遍历视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classify", value = "类别")
    })
    public BaseResponse getVideoByClassify(String classify, @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                           @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return videoService.getVideoByClassify(classify, nodePage, pageSize);
    }
    @GetMapping("/logged/lookTop")
    @ApiOperation("热搜排行榜")
    public BaseResponse lookTop() {
        return videoService.getLookTop();
    }

    @GetMapping("/logged/getAddress")
    @ApiOperation("得到视频地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id"),
            @ApiImplicitParam(name = "videoAddressId", value = "视频地址id")
    })
    public BaseResponse getAddress(Integer videoId, Integer videoAddressId){
        return videosAddressService.getAddress(videoId, videoAddressId);
    }

    @GetMapping("/logged/allTop")
    @ApiOperation("排行榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "类型id")
    })
    public BaseResponse allTop(Integer typeId, @RequestParam(value = "num", defaultValue = "10")Integer num){
        return videoService.allTop(typeId, num);
    }


    @GetMapping("/logged/getSlideshow")
    @ApiOperation("展示轮播图")
    public BaseResponse getSlideshow(@RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                     @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize) {
        return videoService.getSlideshow(nodePage, pageSize);
    }
    @PostMapping("/collectVideo")
    @ApiOperation("收藏视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id")
    })
    public BaseResponse collectVideo(Integer videoId){
        return  collectionsService.collectVideo(videoId);
    }

    @GetMapping("/logged/getHighlights")
    @ApiOperation("取出花絮视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id")
    })
    public BaseResponse getHighlights(Integer videoId,
                                      @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                      @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize) {
        return videosAddressService.getHighlights(videoId, nodePage, pageSize);
    }

    @PostMapping("/addComment")
    @ApiOperation("添加评论")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse addComment(Integer videoId,String comment){
        return videoService.addComment(videoId,comment);
    }

    @PostMapping("/addCommentReply")
    @ApiOperation("回复评论")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse addCommentReply(Integer videoId,String comment,Integer commentId){
        return videoService.addCommentReply(videoId,comment,commentId);
    }

    @PostMapping("/commentLove")
    @ApiOperation("点赞和取消点赞")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse commentLove(Integer videoId,Integer commentId){
        return videoService.commentLove(videoId,commentId);
    }

    @GetMapping("/logged/getCommentPage")
    @ApiOperation("获取一级评论分页")
    public BaseResponse getCommentPage(Integer videoId,
                                       @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                       @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        return videoService.getCommentPage(videoId,nodePage,pageSize);
    }

    @GetMapping("/logged/getCommentReplyPage")
    @ApiOperation("获取二级评论（回复）分页")
    public BaseResponse getCommentReplyPage(Integer commentId,
                                       @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                       @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        return videoService.getCommentReplyPage(commentId,nodePage,pageSize);
    }

    @GetMapping("/getUserLoveCommentList")
    @ApiOperation("获取用户点赞评论的列表")
    public BaseResponse getUserLoveCommentList(Integer videoId){
        return videoService.getUserLoveCommentList(videoId);
    }

    @GetMapping("/logged/getCommentLoveNumber")
    @ApiOperation("获取评论点赞数量")
    public BaseResponse getCommentLoveNumber(Integer commentId){
        return videoService.getCommentLoveNumber(commentId);
    }

    @GetMapping("isCommentLove")
    @ApiOperation("判断是否已经点赞")
    public BaseResponse isCommentLove(Integer commentId){
        return videoService.isCommentLove(commentId);
    }

    @GetMapping("/getLookHistory")
    @ApiOperation("取出搜索的历史记录")
    public BaseResponse getLookHistory() {
        return videoService.lookHistory();
    }

    @DeleteMapping("/deleteUserLookHistory")
    @ApiOperation("删除用户搜索记录")
    public BaseResponse deleteUserLookHistory(String text) {
        return  videoService.deleteUserLookHistory(text);
    }
    @DeleteMapping("/deleteUserLookHistory1")
    @ApiOperation("删除用户搜索记录（所有）")
    public BaseResponse deleteUserLookHistory1() {
        return  videoService.deleteUserLookHistory();
    }
    @PutMapping("/updateVideoAddress")
    @ApiOperation("修改视频信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "视频地址id"),
            @ApiImplicitParam(name = "collections", value = "集数"),
            @ApiImplicitParam(name = "videoType", value = "视频类型 1 正集 0 花絮"),
            @ApiImplicitParam(name = "sidelightsName", value = "视频名"),
            @ApiImplicitParam(name = "vip", value = "是否为vip可看 0 不是 1 是"),

    })
    public BaseResponse updateVideoAddress(Integer id, String collections, MultipartFile file, String sidelightsName, String videoType, Integer vip) {
        return videoService.updateVideoAddress(id,collections,file,sidelightsName,videoType,vip);
    }

    @GetMapping("/logged/getSiftVideo")
    @ApiOperation("获取精选视频")
    public BaseResponse getSiftVideo() {
        return videoService.getSiftVideo();
    }

    @GetMapping("/logged/getsidelights")
    @ApiOperation("取出花絮视频")
    public BaseResponse getsidelights(Integer id) {
        return videosAddressService.getsidelights(id);
    }

    @GetMapping("/logged/lookUserPage")
    @ApiOperation("搜索用户分页")
    public BaseResponse lookUserPage(Integer uploadPower,String text,
                                     @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                     @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return videoService.lookUserPage(uploadPower,text, nodePage, pageSize);
    }

    @GetMapping("/getUserVideo")
    @ApiOperation("得到该用户上传的视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "审核状态 （0审核，1.审核通过，2.审核未通过）")
    })
    public BaseResponse getMyVideo(Integer id, Integer status,@RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return videoService.getUserVideo(id, status,nodePage, pageSize);
    }

    @GetMapping("/getAddress2")
    @ApiOperation("取出视频地址，更新用")
    public BaseResponse getAddress2(Integer id) {
        return videosAddressService.getAddress2(id);
    }
}
