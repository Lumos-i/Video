package com.video.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.controller.webSocket.MyWebSocket;
import com.video.mapper.RemindMapper;
import com.video.mapper.UserMapper;
import com.video.pojo.LoginUser;
import com.video.pojo.Remind;
import com.video.service.RemindService;
import com.video.utils.BaseResponse;
import com.video.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zrq
 * @ClassName RemindServiceImpl
 * @date 2022/10/8 20:35
 * @Description TODO
 */
@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class RemindServiceImpl implements RemindService {
    @Autowired
    private RemindMapper remindMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public BaseResponse getRemind(HttpServletRequest request, Integer remindType, Integer nodePage, Integer pageSize) {
        if (remindType == null) {
            return BaseResponse.nullValue("类型数据不能为空");
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        Page<Remind> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Remind::getUId, new ArrayList<>(Arrays.asList(id, 0)))
                .eq(Remind::getRemindType, remindType).isNull(Remind::getDeleteId).orderByDesc(Remind::getCreateTime);
        Page<Remind> page1 = remindMapper.selectPage(page, wrapper);
        List<Remind> records = page1.getRecords();
        List<Remind> res = new ArrayList<>();
        for (Remind remind : records) {
            if(!StringUtils.isEmpty(remind.getDeleteId())) {
                String[] split = remind.getDeleteId().split(",");
                for (String s : split) {
                    if (s == null || Integer.valueOf(s).equals(id)) {
                        continue;
                    }
                    if (!Integer.valueOf(s).equals(id)) {
                        res.add(remind);
                    }
                }
            }else if(StringUtils.isEmpty(remind.getDeleteId())){
                res.add(remind);
            }
        }
        log.info(res+"");
        page1.setRecords(res);
        log.info(page1.getRecords().toString());
        for (Remind remind : records) {
            Integer id1 = remind.getId();
            Remind remind1 = new Remind();
            remind1.setId(id1);
            remind1.setRemindStatus(1);
            remindMapper.updateById(remind1);
        }
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getRemindNums(HttpServletRequest request, Integer remindType) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getUId, id).eq(Remind::getRemindStatus, 0).eq(remindType != null,Remind::getRemindType, remindType);
        Integer integer = remindMapper.selectCount(wrapper);
        log.info(integer+"");
        return integer == null ? BaseResponse.success("无新消息") : BaseResponse.success(integer);
    }

    @Override
    public BaseResponse deleteRemind(List<Integer> ids) {
        if (ids == null) {
            return BaseResponse.nullValue("id不能为空");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id1 = loginUser.getUser().getId();
        int i = 0;
        for (Integer id : ids) {
            String deleteId = remindMapper.selectById(id).getDeleteId();
            if(deleteId != null) {
                i = remindMapper.updateById(new Remind(id, null, null, null, null, null,null,null,deleteId+","+id1));
            }
            i = remindMapper.updateById(new Remind(id, null, null, null, null, null,null,null,id1+""));
        }
        return i > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse sendRemind(String content) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
//        String id0 = String.valueOf(id);
        if (StringUtils.isEmpty(content)) {
            return BaseResponse.nullValue("数据不能为空");
        }
        int insert;
        try {
            MyWebSocket.sendInfo(null, content);
            insert = remindMapper.insert(new Remind(null, 0, content, null, null, 0, 0,id,null));
        }catch (Exception e){
            return BaseResponse.error("发送失败");
        }
        return insert > 0 ? BaseResponse.success("已发送") : BaseResponse.success("发送失败");
    }

    @Override
    public BaseResponse addRemind(Integer toId, Integer id, String message, String otherId, Integer remindType) {
        if (message == null) {
            return BaseResponse.nullValue("数据不能为空");
        }

        String name = userMapper.selectById(id).getName();
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        JSONObject js = new JSONObject();

        if (remindType == 0 || remindType == 3) {
            remindMapper.insert(new Remind(null, toId, message, null, null, remindType, 0,id,null));
            wrapper.eq(toId != null,Remind::getUId, toId)
                    .eq(id != null,Remind::getSendUid, id)
                    .eq(Remind::getRemindType, remindType)
                    .orderByDesc(Remind::getCreateTime)
                    .last("limit 1");
        }
        if (remindType == 2 || remindType == 1) {
            remindMapper.insert(new Remind(null, toId, message, null, otherId, remindType, 0,id,null));
            wrapper.eq(Remind::getUId, toId)
                    .eq(Remind::getMainId, otherId)
                    .eq(Remind::getSendUid,id)
                    .eq(Remind::getRemindType, remindType)
                    .orderByDesc(Remind::getCreateTime)
                    .last("limit 1");
        }
        Remind remind = remindMapper.selectOne(wrapper);
        Integer id1 = remind.getId();
        Integer remindType1 = remind.getRemindType();
        Date createTime = remind.getCreateTime();
        switch (remindType) {
            //官方提醒
            case 0:
                //关注提醒
            case 3:
                js.put("message", message);
                js.put("userid", id);
                js.put("toUserId", toId);
                js.put("name", name);
                js.put("id",id1);
                js.put("remindType",remindType1);
                js.put("date",createTime);
                break;
                //收藏提醒
            case 1:
            case 2:
                js.put("message", message);
                js.put("userid", id);
                js.put("toUserId", toId);
                js.put("name", name);
                js.put("otherId", otherId);
                js.put("id",id1);
                js.put("remindType",remindType1);
                js.put("date",createTime);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + remindType);
        }
        try {
            MyWebSocket.sendInfo(toId, js.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BaseResponse.success("已推送");
    }

    @Override
    public BaseResponse getSystemNoticePage(Integer nodePage, Integer pageSize) {
        if(nodePage<=0){
            nodePage=1;
        }
        if(pageSize<=0||pageSize>20){
            pageSize=10;
        }
        LambdaQueryWrapper<Remind> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getRemindType,0)
                .isNull(Remind::getMainId)
                .eq(Remind::getUId,0)
                .last("order by create_time desc");
        Page<Remind> page=new Page<>(nodePage,pageSize);
        remindMapper.selectPage(page,wrapper);
        if(page.getRecords().size()!=0){
            return BaseResponse.success("成功",page);
        }
        return BaseResponse.success("未查询到公告");
    }

    @Override
    public BaseResponse deleteSystemNotice(Integer id) {
        if(id<=0){
            return BaseResponse.error("无此id");
        }
        int i;
        try {
            LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Remind::getId, id)
                    .isNull(Remind::getMainId)
                    .eq(Remind::getRemindType, 0);
           i = remindMapper.delete(wrapper);
        }catch (Exception e){
            return BaseResponse.error("代码出错");
        }
        if(i>0){
            return BaseResponse.success("删除成功");
        }
        return BaseResponse.error("删除失败");
    }
}
