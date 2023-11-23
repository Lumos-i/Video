package com.video.controller;

import com.video.limit.AccessLimit;
import com.video.service.*;
import com.video.utils.BaseResponse;
import com.video.vo.UserInfoVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = {("用户接口")})
@SuppressWarnings("rawtypes")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdvertisingService advertisingService;
    @Autowired
    private CollectionsService collectionsService;
    @Autowired
    private RemindService remindService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideosAddressService videosAddressService;
    @Autowired
    private VipPriceService vipPriceService;

    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/outLogin/login")
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名或邮箱"),
            @ApiImplicitParam(name = "password", value = "密码")
    })
    public BaseResponse login(String username, String password) {
        return userService.login(username, password);
    }


    /**
     * 发送验证码
     *
     * @param email
     * @return
     */
    @PostMapping("/logged/sendCode")
    @ApiOperation("发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse sendCode(String email) {
        return userService.sendCode(email);
    }

    @PutMapping("/logged/updatePassword")
    @ApiOperation("修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "email", value = "邮箱"),
            @ApiImplicitParam(name = "code", value = "验证码")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse updatePassword(String email, String code, String password) {
        return userService.updatePassword(email, code, password);
    }

    @PostMapping("/outLogin/register")
    @ApiOperation("注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号"),
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "email", value = "邮箱"),
            @ApiImplicitParam(name = "code", value = "验证码"),
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse register(String username, String password, String email, String code) {
        return userService.register(username, password, email, code);
    }

    @GetMapping("/logged/logOut")
    @ApiOperation(("退出登录"))
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse logOut() {
        return userService.logOut();
    }

    @PutMapping("/upDatePower")
    @ApiOperation("申请成为视频发布者")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id"),
            @ApiImplicitParam(name = "introduction", value = "个人介绍"),
            @ApiImplicitParam(name = "phone", value = "电话")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse updatePower(Integer id, String introduction, String phone) {
        return userService.updatePower(id, introduction, phone);
    }

    @PostMapping("/uploadHeadshot")
    @ApiOperation("上传头像")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse uploadHeadshot(MultipartFile file) {
        return userService.uploadHeadshot(file);
    }

    @PutMapping("/updateUserMessage")
    @ApiOperation("修改用户信息 性别 生日 昵称 地址")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse updateUserMessage(UserInfoVo userInfoVo) {
        return userService.updateUserMessage(userInfoVo);
    }

    @GetMapping("/logged/getUserMessage")
    @ApiOperation("获取用户信息")
    public BaseResponse getUserMessage(HttpServletRequest request) {
        return userService.getUserMessage(request);
    }

    @PostMapping("/sign")
    @ApiOperation("签到")
    @AccessLimit(seconds = 10, maxCount = 5)

    public BaseResponse sign() {
        return userService.sign();
    }

    @GetMapping("/getSign")
    @ApiOperation("获取签到数据")
    public BaseResponse getSign(HttpServletRequest request) {
        return userService.getSign(request);
    }

    @PostMapping("/addFeedBack")
    @ApiOperation("添加反馈")
    @AccessLimit(seconds = 10, maxCount = 5)

    public BaseResponse addFeedBack(HttpServletRequest request, String context, String telephone) {
        return userService.addFeedBack(request, context, telephone);
    }

    @GetMapping("/getFeedBackHistory")
    @ApiOperation("查看历史反馈")
    public BaseResponse getFeedBackHistory(HttpServletRequest request,
                                           @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getFeedBackHistory(request, nodePage, pageSize);
    }

    @GetMapping("/history")
    @ApiOperation("历史记录")
    public BaseResponse history(HttpServletRequest request,
                                @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.history(request, nodePage, pageSize);
    }

    @PostMapping("/addHistory")
    @ApiOperation("添加历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id"),
            @ApiImplicitParam(name = "videoAddressId", value = "视频地址id"),
            @ApiImplicitParam(name = "progress", value = "进度")
    })
    public BaseResponse addHistory(HttpServletRequest request, Integer videoId, Integer videoAddressId, String progress) {
        return userService.addHistory(request, videoId, videoAddressId, progress);
    }

    @DeleteMapping("/deleteHistory")
    @ApiOperation("删除历史记录")
    public BaseResponse deleteHistory(@RequestParam List<Integer> ids) {
        return userService.deleteHistory(ids);
    }

    @GetMapping("/getCollections")
    @ApiOperation("取出收藏")
    public BaseResponse getCollections(HttpServletRequest request,
                                       @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getCollections(request, nodePage, pageSize);
    }

    @DeleteMapping("/deleteCollections")
    @ApiOperation("删除收藏")
    public BaseResponse deleteCollections(@RequestParam List<Integer> ids) {
        return collectionsService.deleteCollections(ids);
    }

    @DeleteMapping("/deleteAllCollection")
    @ApiOperation("删除某个用户所有历史记录")
    public BaseResponse deleteAllCollection(HttpServletRequest request) {
        return userService.deleteAllCollection(request);
    }

    @DeleteMapping("/deleteAllHistory")
    @ApiOperation("删除某个用户所有历史记录")
    public BaseResponse deleteAllHistory(HttpServletRequest request) {
        return userService.deleteAllHistory(request);
    }

    @GetMapping("/logged/getSomeAdvertising")
    @ApiOperation("取出随机的广告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "广告数量")
    })
    public BaseResponse getSomeAdvertising(Integer num) {
        return advertisingService.getSomeAdvertising(num);
    }


    @PostMapping("/addFollow")
    @ApiOperation("关注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "被关注人id")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse addFollow(HttpServletRequest request, Integer id) {
        return userService.addFollow(request, id);
    }

    @GetMapping("/getFans")
    @ApiOperation("取出粉丝列表")
    public BaseResponse getFans(Integer id, @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getFans(id, nodePage, pageSize);
    }

    @GetMapping("/getUserComment")
    @ApiOperation("获取用户的评论（父评论）")
    public BaseResponse getUserComment(Integer id,
                                       @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getUserComment(id, nodePage, pageSize);
    }

    @GetMapping("/getRemind")
    @ApiOperation("取出消息提醒")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remindType", value = "提醒类型 0(官方提醒) 1(收藏提醒) 2（评论提醒） 3(关注提醒"),
    })
    public BaseResponse getRemind(HttpServletRequest request, Integer remindType, @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return remindService.getRemind(request, remindType, nodePage, pageSize);
    }

    @GetMapping("/getRemindNums")
    @ApiOperation("查看有多少未读消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remindType", value = "提醒类型 0(官方提醒) 1(收藏提醒) 2（评论提醒） 3(关注提醒"),
    })
    public BaseResponse getRemindNums(HttpServletRequest request, Integer remindType) {
        return remindService.getRemindNums(request, remindType);
    }

    @DeleteMapping("/deleteRemind")
    @ApiOperation("删除消息提醒")
    public BaseResponse deleteRemind(@RequestParam List<Integer> ids) {
        return remindService.deleteRemind(ids);
    }

    @GetMapping("/getMyVideo")
    @ApiOperation("得到该用户上传的视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "审核状态 （0审核，1.审核通过，2.审核未通过）")
    })
    public BaseResponse getMyVideo(HttpServletRequest request, Integer status, @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getMyVideo(request, status, nodePage, pageSize);
    }

    @DeleteMapping("/deleteVideoInfo")
    @ApiOperation("删除视频信息")
    public BaseResponse deleteVideoInfo(@RequestParam List<Integer> ids) {
        return videoService.deleteVideo(ids);
    }

    @GetMapping("/logged/getUserById")
    @ApiOperation("通过用户id获取用户信息")
    public BaseResponse getUserById(Integer id) {
        return userService.getUserById(id);
    }


    @DeleteMapping("/deleteVideoAddress")
    @ApiOperation("删除视频地址")
    public BaseResponse deleteVideoAddress(@RequestParam List<Integer> ids) {
        return videosAddressService.deleteVideoAddress(ids);
    }

    @GetMapping("/getFollowList")
    @ApiOperation("获取关注列表")
    public BaseResponse getFollowList(Integer id, @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getFollowList(id, nodePage, pageSize);
    }

    @GetMapping("/haveCollected")
    @ApiOperation("判断是否收藏")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse haveCollected(Integer id, Integer videoId) {
        return collectionsService.haveCollected(id, videoId);
    }

    @GetMapping("/haveFollowed")
    @ApiOperation("判断是否关注")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse haveFollowed(Integer id, Integer mainId) {
        return userService.haveFollowed(id, mainId);
    }

    @DeleteMapping("/deleteCommentByUser")
    @ApiOperation("用户删除自己的评论")
    public BaseResponse deleteComment(Integer commentId) {
        return userService.deleteComment(commentId);
    }

    @PostMapping("/reportComment")
    @ApiOperation("举报评论")
    public BaseResponse reportComment(Integer commentId, String cause) {
        return userService.reportComment(commentId, cause);
    }

    @PutMapping("/pointsExchangeVip")
    @ApiOperation("积分兑换VIP")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse pointsExchangeVip(Integer points, Integer vipDays) {
        return userService.pointsExchangeVip(points, vipDays);
    }

    @DeleteMapping("/unsubscribe")
    @ApiOperation("注销账号")
    public BaseResponse unsubscribe() {
        return userService.unsubscribe();
    }

    @GetMapping("/selectVipPriceList")
    @ApiOperation("查看所有vip的价格")
    public BaseResponse selectVipPriceList() {
        return vipPriceService.selectVipPriceList();
    }

    @GetMapping("/logged/search")
    @ApiOperation("拼音转文字")
    public BaseResponse search(String text) {
        return userService.search(text);
    }
}
