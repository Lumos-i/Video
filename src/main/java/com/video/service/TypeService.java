package com.video.service;


import com.video.dto.TypeClassify;
import com.video.utils.BaseResponse;
import com.video.vo.TypeVo;

import java.util.List;


/**
 * @ClassName: TypeService
 * @author: 赵容庆
 * @date: 2022年09月23日 8:26
 * @Description: TODO
 */

@SuppressWarnings("rawtypes")
public interface TypeService {
    /**
     * 添加视频类别
     * @param type
     * @return
     */
    BaseResponse addType(TypeVo type);

    /**
     * 删除视频类别
     * @param id
     * @return
     */
    BaseResponse deleteType(Integer id);

    /**
     * 修改视频类别
     * @param type
     * @param id
     * @return
     */
    BaseResponse updateType(String type, Integer typeClassify,Integer id);

    /**
     * 得到所有的类别
     * @return
     */
    BaseResponse getTypes();

    BaseResponse getParentType();
    List<TypeClassify> getDetailType(Integer id, Integer typeClassify);
}
