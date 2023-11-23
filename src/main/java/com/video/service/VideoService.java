package com.video.service;

import com.video.utils.BaseResponse;
import com.video.vo.VideoInfoVo;
import com.video.vo.VideoInfoVoTwo;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@SuppressWarnings("rawtypes")
public interface VideoService {
    BaseResponse addVideoInfo(String videoName, MultipartFile cover, MultipartFile fragment, String introduction, String actors, String director, String typeName, String allCollections);

    BaseResponse getVideoInfo(Integer id);

    BaseResponse getVideos(String typeName, Integer nodePage, Integer pageSize);

    BaseResponse deleteVideo(List<Integer> ids);

    BaseResponse updateVideoInfo(VideoInfoVoTwo videoInfoVoTwo);

    BaseResponse lookVideo(String text, Integer nodePage, Integer pageSize);

    BaseResponse getTop(Integer id);

    BaseResponse getLookTop();

    BaseResponse allTop(Integer typeId, Integer num);

    BaseResponse slideshow(Integer videoId, Integer slideshow);

    BaseResponse getSlideshow(Integer nodePage, Integer pageSize);

    BaseResponse getVideoByClassify(String classify, Integer nodePage, Integer pageSize);

    BaseResponse addComment(Integer videoId, String comment);

    BaseResponse addCommentReply(Integer videoId, String comment, Integer commentId);

    BaseResponse commentLove(Integer videoId, Integer commentId);

    BaseResponse getCommentPage(Integer videoId, Integer nodePage, Integer pageSize);

    BaseResponse getCommentReplyPage(Integer commentId, Integer nodePage, Integer pageSize);

    BaseResponse getUserLoveCommentList(Integer videoId);

    BaseResponse updateVideoStatus(Integer id, Integer status);

    BaseResponse getAuditVideo(Integer nodePage, Integer pageSize, Integer status);

    BaseResponse lookHistory();

    BaseResponse getAllVideos(Integer nodePage, Integer pageSize);

    BaseResponse updateVideoAddress(Integer id, String collection, MultipartFile file,String sidelightsName, String videoType, Integer vip);

    BaseResponse getCommentLoveNumber(Integer commentId);

    BaseResponse isCommentLove(Integer commentId);

    BaseResponse getSiftVideo();

    BaseResponse lookUserPage(Integer uploadPower,String text, Integer nodePage, Integer pageSize);

    BaseResponse getUserVideo(Integer id, Integer status, Integer nodePage, Integer pageSize);

    BaseResponse deleteUserLookHistory(String text);
    BaseResponse deleteUserLookHistory();
}
