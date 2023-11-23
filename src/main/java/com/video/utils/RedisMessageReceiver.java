package com.video.utils;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.video.mapper.UserMapper;
import com.video.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RedisMessageReceiver {
    @Resource
    private UserMapper userMapper;
    /**
     * 接收redis消息，并处理
     *
     * @param message 过期的redis key
     */
    public void receiveMessage(String message) {
        if("vipOutTime:".equals(message.substring(0, 11))){
            int id = Integer.parseInt(message.substring(11));
            LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<User>();
            userWrapper.eq(User::getId,id)
                    .set(User::getVip,0);
            int i = userMapper.update(null, userWrapper);
            if(i>0){
                User user = userMapper.selectById(id);
                log.info("用户昵称为:"+user.getName()+"的VIP已过期！");
            }else {
                log.info("使用监听redis过期键修改用户vip状态出错！");
            }
        }
        if("userBanned:".equals(message.substring(0, 11))){
            int id = Integer.parseInt(message.substring(11));
            User user = userMapper.selectById(id);
            String head = "封号结束通知";
            String body = "尊敬的"+user.getName()+"您好！" +
                    "您的账号封禁时间已结束，您可以继续正常使用该账号，但请规范使用账号以免再次封号！";
            int i = SendMailVerify.MailMessage(user.getEmail(), head, body);
            if(i==1){
                log.info("用户昵称为:"+user.getName()+"的账号解封通知已发送！");
            }else if(i==0){
                log.info("用户解封邮件发送失败！");
            }else {
                log.info("邮件信息不完整，未能发送！");
            }
        }
    }

}
