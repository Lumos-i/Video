package com.video.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;
@Component
@Slf4j
public class SendMailVerify {
    public static JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setUsername("1002800982@qq.com");
        mailSender.setPassword("eqxzugqvxccsbbfc");
        return mailSender;
    }

    public static int MailVerify(String mail){
        boolean result = RegExpUtil.matchEmail(mail);
        int verify = 0;
        if(result) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("绑定邮箱验证！");
            verify = (int) ((Math.random() * 9 + 1) * 100000);
            message.setText(String.valueOf(verify));
            message.setTo(mail);
            message.setFrom("1002800982@qq.com");
            javaMailSender().send(message);
            log.info(verify + "========");
        }
        return verify;
    }

    public static int MailMessage(String mail,String head,String body){
        if(head.isEmpty() || body.isEmpty()){
            log.info("邮件主题或标题为空！");
            return -1;
        }
        boolean result = RegExpUtil.matchEmail(mail);
        if(result) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(head);
            message.setText(body);
            message.setTo(mail);
            message.setFrom("1002800982@qq.com");
            javaMailSender().send(message);
            return 1;
        }
        return 0;
    }
}
