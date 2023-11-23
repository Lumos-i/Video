package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.pojo.FeedBack;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: FeedBackMapper
 * @author: 赵容庆
 * @date: 2022年09月24日 16:08
 * @Description: TODO
 */

@Mapper
public interface FeedBackMapper extends BaseMapper<FeedBack> {
}
