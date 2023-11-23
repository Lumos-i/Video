package com.video.service;


import com.video.pojo.Orders;

import com.video.utils.BaseResponse;
import com.video.vo.UserInfoVo;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface UserService {
    public BaseResponse login(String username, String password);
    public BaseResponse register(String username, String password, String email, String code);
    public BaseResponse sendCode(String email);

    BaseResponse updatePassword(String email, String code, String password);

    BaseResponse logOut();

    BaseResponse updatePower(Integer id, String introduction, String phone);

    BaseResponse uploadHeadshot(MultipartFile file);

    BaseResponse updateUserMessage(UserInfoVo userInfoVo);

    BaseResponse getUserMessage(HttpServletRequest request);

    BaseResponse addFeedBack(HttpServletRequest request, String context,String telephone);

    BaseResponse getFeedBackHistory(HttpServletRequest request,Integer nodePage,Integer pageSize);

    BaseResponse getCollectionsList(Integer userId);

    BaseResponse sign();

    BaseResponse getSign(HttpServletRequest request);

    BaseResponse history(HttpServletRequest request, Integer nodePage, Integer pageSize);

    BaseResponse addHistory(HttpServletRequest request, Integer videoId, Integer videoAddressId, String progress);

    BaseResponse deleteHistory(List<Integer> ids);

    BaseResponse getCollections(HttpServletRequest request, Integer nodePage, Integer pageSize);

    Orders createOrder(HttpServletRequest httpServletRequest,Integer vipDay);

    BaseResponse deleteAllHistory(HttpServletRequest request);

    BaseResponse deleteAllCollection(HttpServletRequest request);

    BaseResponse addFollow(HttpServletRequest request, Integer id);

    BaseResponse getFans(Integer id, Integer nodePage, Integer pageSize);

    BaseResponse getUserComment(Integer id, Integer nodePage, Integer pageSize);

    BaseResponse getMyVideo(HttpServletRequest request, Integer status,Integer nodePage, Integer pageSize);

    BaseResponse getUserById(Integer id);

    BaseResponse getFollowList(Integer id, Integer nodePage, Integer pageSize);

    BaseResponse haveFollowed(Integer id, Integer mainId);

    List<Integer> selectAdmin();

    BaseResponse deleteComment(Integer commentId);

    BaseResponse reportComment(Integer commentId, String cause);

    BaseResponse pointsExchangeVip(Integer points, Integer vipDays);

    BaseResponse unsubscribe();
    BaseResponse unsubscribe1(List<Integer> ids);

    BaseResponse onlineUser();

    BaseResponse getUserApplication(Integer status, Integer nodePage, Integer pageSize);

    BaseResponse updateApplication(Integer id, Integer status);

    BaseResponse getAllFeedBack(Integer nodePage, Integer pageSize);

    BaseResponse deleteFeedBack(List<Integer> ids);

    BaseResponse search(String text);

    BaseResponse updateWeight(Integer id, double weight);

    BaseResponse updateUserState(Integer id,Integer state, Integer days);

    BaseResponse lookUserPage(String name, String sex, String username, String email, String address,Integer nodePage,Integer pageSize);
}
