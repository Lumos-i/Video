package com.video.controller;

import com.video.limit.AccessLimit;
import com.video.service.*;
import com.video.utils.BaseResponse;
import com.video.vo.AdvertisingVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName: AdminController
 * @author: 赵容庆
 * @date: 2022年09月24日 15:46
 * @Description: 管理员接口
 */

@RestController
@RequestMapping("/admin")
@Api(tags = {("管理员接口")})
@PreAuthorize("hasAnyAuthority('1')")
@SuppressWarnings("rawtypes")
public class AdminController {
    @Autowired
    private VideosAddressService videosAddressService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private AdvertisingService advertisingService;
    @Autowired
    private VipPriceService vipPriceService;
    @Autowired
    private RemindService remindService;
    @Autowired
    private UserService userService;
    @Resource
    private FilterWordsService filterWordsService;

    @PutMapping("/checkVideo")
    @ApiOperation("审核视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "视频id"),
            @ApiImplicitParam(name = "status", value = "审核状态（0审核中，1.审核通过，2.审核未通过）")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse checkVideo(Integer id, Integer status) {
        return videosAddressService.updateStatus(id, status);
    }


    @PutMapping("/updateVip")
    @ApiOperation("修改是否为vip视频")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse updateVip(List<Integer> ids, List<Integer> vips) {
        return videosAddressService.updateVip(ids,vips);
    }


    @DeleteMapping("/deleteVideoInfo")
    @ApiOperation("删除视频信息")
    public BaseResponse deleteVideoInfo(@RequestParam List<Integer> ids) {
        return videoService.deleteVideo(ids);
    }


    @DeleteMapping("/deleteVideoAddress")
    @ApiOperation("删除视频地址")
    public BaseResponse deleteVideoAddress(@RequestParam List<Integer> ids) {
        return videosAddressService.deleteVideoAddress(ids);
    }

    @PutMapping("/showSlideshow")
    @ApiOperation("设置主页轮播图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id"),
            @ApiImplicitParam(name = "slideshow", value = "状态 1为轮播图 0未设置")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse slideshow(Integer videoId, Integer slideshow) {
        return videoService.slideshow(videoId, slideshow);
    }

    @PostMapping("/addAdvertising")
    @ApiOperation("添加广告")

    public BaseResponse addAdvertising(AdvertisingVo advertisingVo) {
        return advertisingService.addAdvertising(advertisingVo);
    }
    @DeleteMapping("/deleteAdvertising")
    @ApiOperation("删除广告")
    public BaseResponse deleteAdvertising(@RequestParam List<Integer> ids) {
        return advertisingService.deleteAdvertising(ids);
    }

    @GetMapping("/getAllAdvertising")
    @ApiOperation("取出所有广告")
    public BaseResponse getAllAdvertising( @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                           @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return advertisingService.getAllAdvertising(nodePage, pageSize);
    }
    @GetMapping("/logged/getVideoByClassify")
    @ApiOperation("根据分类遍历视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classify", value = "类别")
    })
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse getVideoByClassify(String classify, @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                           @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return videoService.getVideoByClassify(classify, nodePage, pageSize);
    }
    @PostMapping("/addVipPrice")
    @ApiOperation("添加vip的价格")
    public BaseResponse addVipPrice(Integer vipDays,Float vipPrice,Integer points){
        return vipPriceService.addVipPrice(vipDays,vipPrice,points);
    }

    @DeleteMapping("/deleteVipPrice")
    @ApiOperation("删除vip的价格")
    public BaseResponse deleteVipPrice(Integer id){
        return vipPriceService.deleteVipPrice(id);
    }

    @PutMapping("/updateVipPrice")
    @ApiOperation("修改vip的价格")
    public BaseResponse updateVipPrice(Integer vipDays,Float vipPrice,Integer points){
        return vipPriceService.updateVipPrice(vipDays,vipPrice,points);
    }

    @GetMapping("/getAuditVideo")
    @ApiOperation("获取不同审核状态的视频:0 未审核,1 通过,2 未通过 不传则为获取所有状态的视频")
    public BaseResponse getAuditVideo(Integer status,
                                      @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                      @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        return videoService.getAuditVideo(nodePage,pageSize,status);
    }

    @PostMapping("/updateVideoStatus")
    @ApiOperation("修改视频的审核状态:1 通过,2 不通过")
    public BaseResponse updateVideoStatus(Integer id,Integer status){
        return videoService.updateVideoStatus(id,status);
    }

    @PostMapping("/sendRemind")
    @ApiOperation("管理员发送公告")
    public BaseResponse sendRemind(String content) {
        return remindService.sendRemind(content);
    }

    @GetMapping("/getSystemNoticePage")
    @ApiOperation("查看所有公告分页")
    public BaseResponse getRemindPage(@RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                      @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        return remindService.getSystemNoticePage(nodePage,pageSize);
    }

    @DeleteMapping("/deleteSystemNotice")
    @ApiOperation("删除公告")
    public BaseResponse deleteSystemNotice(Integer id){
        return remindService.deleteSystemNotice(id);
    }

    @GetMapping("/getUserApplication")
    @ApiOperation("得到用户成为发布者的请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "2 审核中 1 审核通过")
    })
    public BaseResponse getUserApplication(Integer status,
                                           @RequestParam(value = "nodePage", defaultValue = "1")Integer nodePage,
                                           @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {
        return userService.getUserApplication(status,nodePage,pageSize);
    }

    @PutMapping("/updateApplication")
    @ApiOperation("对成为发布者的请求进行审核")
    public BaseResponse updateApplication(Integer id, Integer status) {
        return userService.updateApplication(id, status);
    }
    @GetMapping("/getAllFeedBack")
    @ApiOperation("取出所有反馈")
    public BaseResponse getAllFeedBack( @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.getAllFeedBack(nodePage,pageSize);
    }
    @DeleteMapping("/unsubscribe")
    @ApiOperation("注销账号")
    public BaseResponse unsubscribe(List<Integer> ids) {
        return userService.unsubscribe1(ids);
    }

    @PutMapping("/updateWeight")
    @ApiOperation("更新广告权重")
    public BaseResponse updateWeight(Integer id, double weight) {
        return userService.updateWeight(id, weight);
    }

    @DeleteMapping("/deleteFeedBack")
    @ApiOperation("删除反馈")
    public BaseResponse deleteFeedBack(@RequestParam List<Integer> ids) {
        return userService.deleteFeedBack(ids);
    }

    @PostMapping("/addFilterWords")
    @ApiOperation("增加需要过滤的词")
    public BaseResponse addFilterWords(String... strings) throws IOException {
        return filterWordsService.addFilterWords(strings);
    }

    @PutMapping("/updateUserState")
    @ApiOperation("修改用户的账号状态 0为正常，1为封禁")
    @AccessLimit(seconds = 10, maxCount = 5)
    public BaseResponse updateUserState(Integer id,Integer state,Integer days){
        return userService.updateUserState(id,state,days);
    }

    @GetMapping("/lookUserPage")
    @ApiOperation("搜索用户分页")
    public BaseResponse lookUserPage(String name,String sex,String username,String email,String address,
                                     @RequestParam(value = "nodePage", defaultValue = "1") Integer nodePage,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return userService.lookUserPage(name,sex,username,email,address,nodePage,pageSize);
    }
}
