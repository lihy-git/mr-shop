package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author shenyaqi
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {

        CategoryEntity parentCateEntity = new CategoryEntity();

        parentCateEntity.setId(categoryEntity.getParentId());

        parentCateEntity.setIsParent(1);

        categoryMapper.updateByPrimaryKeySelective(parentCateEntity);

        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> editCategory(CategoryEntity categoryEntity) {

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteCategory(Integer id) {

        if (ObjectUtil.isNull(id)) {

            return this.setResultError("当前id不存在");

        }

        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        if(categoryEntity.getIsParent() == 1){

//            return this.setResultError("当前节点为父节点,不能删除");
            return this.setResultError(HTTPStatus.OPERATION_ERROR,"当前节点为父节点,不能删除");

        }

        Integer count = categoryMapper.getByCategoryId(id);
        if(count > 0){
            return this.setResultError(HTTPStatus.OPERATION_ERROR,"当前分类被品牌绑定,不能删除");
        }

        Example example = new Example(CategoryEntity.class);

        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());

        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        if(!list.isEmpty() && list.size() == 1){

            CategoryEntity parentCateEntity = new CategoryEntity();

            parentCateEntity.setId(categoryEntity.getParentId());

            parentCateEntity.setIsParent(0);

            categoryMapper.updateByPrimaryKeySelective(parentCateEntity);

        }

        categoryMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);

        return this.setResultSuccess(byBrandId);
    }

}