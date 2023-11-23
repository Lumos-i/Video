package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.dto.OneHistory;
import com.video.pojo.History;
import com.video.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lenovo
 * @ClassName HistoryMapper
 * @date 2022/10/3 16:54
 * @Description TODO
 */
@Mapper
public interface HistoryMapper extends BaseMapper<History> {
    OneHistory getOneHistory(Integer id);
}
