package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.pojo.Remind;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zrq
 * @ClassName RemindMapper
 * @date 2022/10/8 20:36
 * @Description TODO
 */
@Mapper
public interface RemindMapper extends BaseMapper<Remind> {

}
