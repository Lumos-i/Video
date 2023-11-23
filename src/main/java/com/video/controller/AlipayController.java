package com.video.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.video.config.AlipayConfig;
import com.video.config.JedisConfig;
import com.video.limit.AccessLimit;
import com.video.mapper.OrderMapper;
import com.video.mapper.UserMapper;
import com.video.pojo.Orders;
import com.video.pojo.User;
import com.video.service.UserService;
import com.video.utils.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/alipay")
@Slf4j
@Api(tags = {("支付接口")})
public class AlipayController {
    private static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    private static final String RETURN_URL = "http://110.40.205.103:3500/myInfo";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";//签名方式

    @Resource
    private AlipayConfig aliPayConfig;
    @Resource
    private UserService userService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private JedisConfig jedisConfig;

    @GetMapping("/rechargeVip")// http://110.40.205.103:8080/alipay/logged/notify
    @ApiOperation("用户充值vip")//   lcgwtm0862@sandbox.com
    @ResponseBody
//    @AccessLimit(seconds = 10, maxCount = 5)
    public void rechargeVip(HttpServletRequest httpServletRequest,Integer vipDays,HttpServletResponse httpResponse) throws IOException {
        Orders order = userService.createOrder(httpServletRequest, vipDays);
        if(order==null){
            log.info("创建订单出错误");
            return;
        }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(RETURN_URL);
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getOrderOn());  // 我们自己生成的订单编号
        bizContent.set("total_amount", order.getOrderTotal()); // 订单的总金额
        bizContent.set("subject", order.getOrderTitle());   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        String form = "";// 执行请求，拿到响应的结果，返回给浏览器
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("支付出错");
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/logged/notify")  // 注意这里必须是POST接口
    @ApiOperation("支付宝回调接口")
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String outTradeNo = params.get("out_trade_no");
            String alipayTradeNo = params.get("trade_no");
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent
                    (content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            if (checkSignature) {// 支付宝验签
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();// 查询订单
                wrapper.eq(Orders::getOrderOn, outTradeNo)
                        .set(Orders::getAlipayOn, alipayTradeNo)
                        .set(Orders::getOrderState, "已支付");
                int update = orderMapper.update(null, wrapper);
                if (update > 0) {
                    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Orders::getOrderOn, outTradeNo)
                            .eq(Orders::getAlipayOn, alipayTradeNo)
                            .eq(Orders::getOrderState, "已支付")
                            .eq(Orders::getOrderTitle, params.get("subject"));
                    Orders orders = orderMapper.selectOne(queryWrapper);
                    if(orders!=null){
                        User user= redisCache.getCacheObject("login:"+orders.getUserId());
                        LocalDateTime time= user.getVipOutTime();
                        int num = time.compareTo(LocalDateTime.now());
                        LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<>();
                        userWrapper.eq(User::getId, orders.getUserId())
                                .set(User::getVip,1)
                                .set(num > 0,User::getVipOutTime,time.plus(Period.ofDays(orders.getVipTime())))
                                .set(num <= 0,User::getVipOutTime, LocalDateTime.now().plus(Period.ofDays(orders.getVipTime())));
                        //将用户vip过期信息存到数据库1中，并设置过期时间
                        long timeOut;
                        if(num>0){
                            Duration duration=Duration.between
                                    (LocalDateTime.now(),time.plus(Period.ofDays(orders.getVipTime())));
                            timeOut = (duration.toMinutes())*60;
                        }else {
                            timeOut = (orders.getVipTime())*24*60*60;
                        }
                        Jedis jedis = jedisConfig.getJedis();
                        jedis.setex("vipOutTime:"+orders.getUserId(), Math.toIntExact(timeOut),"");

                        //添加提醒
//                        String content1 = "恭喜您已开通vip";
//                        Remind remind = new Remind(null, userId, content1, null, null, 0, 0);
                        int i = userMapper.update(null, userWrapper);
                        if(i>0){
                            user = userMapper.selectById(orders.getUserId());
                            redisCache.setCacheObject("login:" + user.getId(), user, 60 * 24, TimeUnit.MINUTES);
                            System.out.println(orders.getOrderTitle()+"成功");
                            System.out.print("success");
                            return "success";
                        }else {
                            System.out.println(orders.getOrderTitle()+"失败");
                            System.out.print("fail");
                            return "fail";
                        }
                    }else {
                        System.out.println("未查询到指定订单");
                        System.out.print("fail");
                        return "fail";
                    }
                } else {
                    System.out.print("fail");
                    return "fail";
                }
            }
        }
        System.out.print("fail");
        return "fail";
    }
}
