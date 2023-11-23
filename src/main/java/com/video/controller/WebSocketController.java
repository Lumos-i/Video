package com.video.controller;

import com.video.controller.webSocket.MyWebSocket;

import com.video.service.RemindService;
import com.video.service.UserService;
import com.video.utils.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * @author zrq
 * @ClassName WebSocketController
 * @date 2022/10/4 10:43
 * @Description webSocket接口
 */
@RestController
@RequestMapping("/socket")
@PreAuthorize("hasAnyAuthority('0','1')")
@SuppressWarnings("rawtypes")
@Api(tags = {("websocket")})
@Slf4j
public class WebSocketController {
    @Autowired
    private UserService userService;
    @Autowired
    private RemindService remindService;


    @PostMapping("/socket/push/{id}")
    @ApiOperation("发送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id，不填默认全部"),
            @ApiImplicitParam(name = "message", value = "消息")

    })
    public String pushToWeb(Integer id, String message) {
        try {
            MyWebSocket.sendInfo(id,message);
        } catch (IOException e) {
            e.printStackTrace();
            return "推送失败";
        }
        return "推送成功"+id;
    }

    @PostMapping("/addRemind")
    @ApiOperation("发送提醒")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toId", value = "要发送给那个用户的id（可不填，默认全部发送）"),
            @ApiImplicitParam(name = "id", value = "用户的id"),
            @ApiImplicitParam(name = "message", value = "消息"),
            @ApiImplicitParam(name = "otherId", value = "评论或者视频id(可不填)"),
            @ApiImplicitParam(name = "remindType", value = "消息类型")

    })
    public BaseResponse addRemind(Integer toId, Integer id, String message, String otherId, Integer remindType) {
        return remindService.addRemind(toId,id,message,otherId,remindType);
    }

    @GetMapping("/onlineUser")
    @ApiOperation("在线的用户")
    public BaseResponse onlineUser() {
        return userService.onlineUser();
    }
}
