package com.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.video.dto.TypeClassify;
import com.video.dto.TypeListDto;
import com.video.mapper.TypeMapper;
import com.video.mapper.VideoMapper;
import com.video.pojo.Type;
import com.video.pojo.Videos;
import com.video.service.TypeService;
import com.video.utils.BaseResponse;
import com.video.utils.RedisCache;
import com.video.vo.TypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: TypeServiceImpl
 * @author: 赵容庆
 * @date: 2022年09月23日 8:30
 * @Description: TODO
 */

@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeMapper typeMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private VideoMapper videoMapper;
    @Override
    public BaseResponse addType(TypeVo type) {
        if (!StringUtils.isEmpty(type.getTypeName())) {
            LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Type::getTypeName,type.getTypeName());
            if (typeMapper.selectList(wrapper).size() > 0) {
                return BaseResponse.error("该类型名已存在");
            }
            int insert = typeMapper.insert(new Type(null, type.getTypeName(), type.getTypeClassify(), type.getTypeLevel(), type.getLastId()));
            redisCache.deleteObject("type");
            return insert > 0 ? BaseResponse.success("添加成功") : BaseResponse.error("服务器错误");
        }else {
            return BaseResponse.nullValue("数据不能为空");
        }
    }

    @Override
    public BaseResponse deleteType(Integer id) {
        if (!StringUtils.isEmpty(id)) {
            String typeName = typeMapper.selectById(id).getTypeName();
            LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Videos::getTypeName,typeName);
            List<Videos> videos = videoMapper.selectList(wrapper);
            if (!videos.isEmpty()) {
                return BaseResponse.success("该类型下存在视频，请清空后再进行删除");
            }
            int i = typeMapper.deleteById(id);
            redisCache.deleteObject("type");
            return i > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
        }else {
            return BaseResponse.nullValue("数据不能为空");
        }
    }

    @Override
    public BaseResponse updateType(String typeName, Integer typeClassify, Integer id) {
        if (!StringUtils.isEmpty(id) || !StringUtils.isEmpty(typeName)) {
            String oldTypeName =typeMapper.selectById(id).getTypeName();
            int i = typeMapper.updateById(new Type(id, typeName,typeClassify,null,null));
            LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Videos::getTypeName,oldTypeName);
            List<Videos> videos = videoMapper.selectList(wrapper);
            String newTypeName = "";
            for (Videos videos1 : videos) {
                String[] split = videos1.getVideoName().split("，");
                for (int j = 0; j < split.length; j++) {
                    if (oldTypeName.equals(split[j])) {
                        split[j] = typeName;
                    }
                    newTypeName += split[j]+",";
                }
                newTypeName = newTypeName.substring(0, newTypeName.length() - 1);
                Videos newVideos = new Videos();
                newVideos.setId(videos1.getId());
                newVideos.setTypeName(newTypeName);
                videoMapper.updateById(newVideos);
            }
            redisCache.deleteObject("type");
            return i > 0 ? BaseResponse.success("更新成功") : BaseResponse.error("服务器错误");
        }else {
            return BaseResponse.nullValue("数据不能为空");
        }
    }

    @Override
    public BaseResponse getTypes() {
//        List<TypeDto> types;
//        types = redisCache.getCacheList("types");
//        if(types.isEmpty() || types.size() == 0) {
//            types = typeMapper.getType();
//            redisCache.setCacheList("types", types);
//        }
//        return BaseResponse.success(types);
            LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Type::getTypeLevel, 0);
            List<TypeListDto> res = new ArrayList<>();
            List<Type> types = typeMapper.selectList(wrapper);
            for (Type type : types) {
                Integer id = type.getId();
                TypeListDto typeListDto = new TypeListDto();
                typeListDto.setId(type.getId());
                typeListDto.setTypeName(type.getTypeName());
                typeListDto.setTypeLevel(type.getTypeLevel());
                typeListDto.setTypeClassify(type.getTypeClassify());
                // 0 主指标  1 详细分类  2 国家  3 制作方 4 年份 5 付费类型
                typeListDto.setChildType(typeMapper.getDetailType(id, 1));
                typeListDto.setCountryType(typeMapper.getDetailType(id, 2));
                typeListDto.setAuthorType(typeMapper.getDetailType(id, 3));
                typeListDto.setYearType(typeMapper.getDetailType(id, 4));
                typeListDto.setVipType(Arrays.asList("付费", "免费"));
                res.add(typeListDto);
            }
        return BaseResponse.success(res);
    }

    @Override
    public BaseResponse getParentType() {
        QueryWrapper<Type> wrapper = new QueryWrapper<>();
        wrapper.eq("type_level",0);
        List<Type> type = typeMapper.selectList(wrapper);
        return BaseResponse.success(type);
    }

    @Override
    public List<TypeClassify> getDetailType(Integer id, Integer typeClassify) {
        return typeMapper.getDetailType(id, typeClassify);
    }
}
