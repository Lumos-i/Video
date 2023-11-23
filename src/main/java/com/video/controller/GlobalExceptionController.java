package com.video.controller;

import com.video.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zrq
 * @ClassName GlobalExceptionController
 * @date 2022/10/15 8:10
 * @Description 全局异常处理
 */
@Slf4j
@ControllerAdvice
@SuppressWarnings("rawtypes")
public class GlobalExceptionController {


    /**
     *
     * @param request
     * @param ru
     * @param hm
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse tokenException(HttpServletRequest request, RuntimeException ru, HandlerMethod hm){
        log.info("在那个类的错误:{}",hm.getMethod());
        return BaseResponse.error("数据操作错误");
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public BaseResponse exceptionHandler(HttpServletRequest req, NullPointerException e){
        log.info("发生空指针异常！原因是:",e);
        return BaseResponse.nullValue("数据为空");
    }

    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public BaseResponse exceptionHandler(HttpServletRequest req, Exception e){
        log.info("未知异常！原因是:",e);
        return BaseResponse.error("未知异常");
    }
}