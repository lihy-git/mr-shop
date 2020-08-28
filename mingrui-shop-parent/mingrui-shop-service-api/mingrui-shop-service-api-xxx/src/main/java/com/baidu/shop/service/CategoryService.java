package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/add")
    Result<JSONObject> saveCategory(@RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "修改分类")
    @PutMapping(value = "category/edit")
    Result<JSONObject> editCategory(@RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "删除分类")
    @DeleteMapping(value = "category/del")
    Result<JSONObject> deleteCategory(Integer id);

}
