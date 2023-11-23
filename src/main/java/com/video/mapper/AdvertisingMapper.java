package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.pojo.Advertising;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zrq
 * @ClassName AdvertisingMapper
 * @date 2022/10/6 9:49
 * @Description TODO
 */
@Mapper
public interface AdvertisingMapper extends BaseMapper<Advertising> {

}
