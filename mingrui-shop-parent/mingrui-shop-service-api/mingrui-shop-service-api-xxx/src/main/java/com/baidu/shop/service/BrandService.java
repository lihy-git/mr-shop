package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {

    @GetMapping(value = "brand/getBrandInfo")
    @ApiOperation(value = "查询品牌信息")
    Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);

    @PostMapping(value = "brand/save")
    @ApiOperation(value = "新增品牌信息")
    Result<JsonObject> saveBrand(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @PutMapping(value = "brand/save")
    @ApiOperation(value = "修改品牌信息")
    Result<JsonObject> editBrand(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @DeleteMapping(value = "brand/delete")
    @ApiOperation(value = "删除品牌信息")
    Result<JsonObject> deleteBrand(Integer id);

    @ApiOperation(value="通过分类id获取品牌")
    @GetMapping(value = "brand/getBrandByCategory")
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid);

    @GetMapping(value = "brand/getBrandByIdList")
    @ApiOperation(value = "通过品牌Id查品牌信息")
    Result<List<BrandEntity>> getBrandByIdList(@RequestParam String brandIdsStr);

}
