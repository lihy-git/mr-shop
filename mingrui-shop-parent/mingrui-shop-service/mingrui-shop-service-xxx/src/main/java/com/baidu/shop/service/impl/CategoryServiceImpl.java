package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
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

        //return new Result<List<CategoryEntity>>(HTTPStatus.OK,"",list);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {

        //先通过parentid去查询对应的parent节点的IsParent是否为1
        //如果不为1的话需要将IsParent改为1

        //通过新增节点的父id将父节点的parent状态改为1

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

        //验证传入的id是否有效,并且查询出来的数据对接下来的程序有用

        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        //判断当前节点是否为父节点

        if(categoryEntity.getIsParent() == 1){

            return this.setResultError("当前节点为父节点,不能删除");

        }

        //构建条件查询 通过当前被删除节点的parentid查询数据
        Example example = new Example(CategoryEntity.class);

        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());

        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        //如果查询出来的数据只有一条
        if(!list.isEmpty() && list.size() == 1){//将父节点的isParent状态改为0

            CategoryEntity parentCateEntity = new CategoryEntity();

            parentCateEntity.setId(categoryEntity.getParentId());

            parentCateEntity.setIsParent(0);

            categoryMapper.updateByPrimaryKeySelective(parentCateEntity);

        }

        categoryMapper.deleteByPrimaryKey(id);//执行删除

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);

        return this.setResultSuccess(byBrandId);
    }

}