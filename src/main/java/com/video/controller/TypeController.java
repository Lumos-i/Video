package com.video.controller;

import com.video.service.TypeService;
import com.video.utils.BaseResponse;
import com.video.vo.TypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: TypeController
 * @author: 赵容庆
 * @date: 2022年09月23日 8:47
 * @Description: 类型接口
 */
@RestController
@RequestMapping("/type")
@Slf4j
@Api(tags = {("类型接口")})
@SuppressWarnings("rawtypes")
public class TypeController {
    @Autowired
    private TypeService typeService;

    @PostMapping("/addType")
    @ApiOperation("添加类型")
    public BaseResponse addType(TypeVo type){
        return typeService.addType(type);
    }

    @PutMapping("/updateType")
    @ApiOperation("修改类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "类型id"),
            @ApiImplicitParam(name = "typeClassify", value = "类型的详细分类  1 子级分类 2 国家分类 3 制作方分类 4 年份分类 5 是否付费"),
            @ApiImplicitParam(name = "type", value = "新类型名")

    })
    public BaseResponse updateType(Integer id,Integer typeClassify, String type) {
        return typeService.updateType(type,typeClassify, id);
    }
    @DeleteMapping ("/deleteType")
    @ApiOperation("删除类型")
    public BaseResponse deleteType(Integer id){
        return typeService.deleteType(id);
    }
    @GetMapping ("/logged/getType")
    @ApiOperation("取出类型")
    public BaseResponse getType(){
        return typeService.getTypes();
    }
    @GetMapping("/logged/getParentType")
    @ApiOperation("取出父级类型")
    public BaseResponse getParentType() {
        return typeService.getParentType();
    }

}
