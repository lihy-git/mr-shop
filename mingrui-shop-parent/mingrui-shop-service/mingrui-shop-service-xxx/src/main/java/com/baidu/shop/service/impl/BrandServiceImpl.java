package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author lihongyang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序/条件查询
        Example example = new Example(BrandEntity.class);

        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());

        if(StringUtil.isNotEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%" + brandDTO.getName() + "%");

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        //返回
        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrand(BrandDTO brandDTO) {

        //brandMapper.insertSelective(BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class));

        //java中一个方法的大小最多是一整个屏幕
        //新增品牌并且可以返回主键
//        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);

        //获取品牌名称
        //获取品牌第一个字符
        //吧字符转为Pinyin
        //获取拼音的首字母
        //都转成大写

        /*String name = brandEntity.getName();
        char c = name.charAt(0);
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(c),PinyinUtil.TO_FIRST_CHAR_PINYIN);
        brandEntity.setLetter(upperCase.charAt(0));*/

//        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
//                , PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));
//        brandMapper.insertSelective(brandEntity);
//
//        if(brandDTO.getCategory().contains(",")){

            //分割 得到数组, 批量新增
            //String[] cidArr = brandDTO.getCategory().split(",");

            //List<String> list = Arrays.asList(cidArr);

           /* List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
            list.stream().forEach(cid -> {
                CategoryBrandEntity entity = new CategoryBrandEntity();
                entity.setCategoryId(StringUtil.toInteger(cid));
                entity.setBrandId(brandEntity.getId());
                categoryBrandEntities.add(entity);
            });*/

            //通过split方法分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用JDK1,8的stream
            //使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型
//            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
//                    .stream().map(cid -> {
//
//                        CategoryBrandEntity entity = new CategoryBrandEntity();
//                        entity.setCategoryId(StringUtil.toInteger(cid));
//                        entity.setBrandId(brandEntity.getId());
//
//                        return entity;
//                    }).collect(Collectors.toList());

            //批量新增
//            categoryBrandMapper.insertList(categoryBrandEntities);

            /*for (String s : cidArr) {
                CategoryBrandEntity entity = new CategoryBrandEntity();
                entity.setCategoryId(StringUtil.toInteger(s));
                entity.setBrandId(brandEntity.getId());
                categoryBrandMapper.insertSelective(entity);
            }*/

//            }else{
//                //新增
//                CategoryBrandEntity entity = new CategoryBrandEntity();
//
//                entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
//                entity.setBrandId(brandEntity.getId());
//
//                categoryBrandMapper.insertSelective(entity);
//            }
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        this.insertCategoryAndBrand(brandDTO, brandEntity);

        return this.setResultSuccess();

    }

    @Transactional
    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //执行修改操作
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //通过brandID删除中间表的数据
//        Example example = new Example(CategoryBrandEntity.class);
//        example.createCriteria().andEqualTo("brandId",brandEntity.getId());
//        categoryBrandMapper.deleteByExample(example);
        this.deleteCategoryAndBrand(brandEntity.getId());

        //新增新的数据
        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }



    private void insertCategoryAndBrand(BrandDTO brandDTO, BrandEntity brandEntity){

        if(brandDTO.getCategory().contains(",")){

            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(cid -> {
                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

            return entity;
        }).collect(Collectors.toList());

        categoryBrandMapper.insertList(categoryBrandEntities);
        }else{
            CategoryBrandEntity entity = new CategoryBrandEntity();
            entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            entity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(entity);
        }
    }

    @Transactional
    @Override
    public Result<JsonObject> deleteBrand(Integer id) {

        //删除品牌
        brandMapper.deleteByPrimaryKey(id);
        //关系?????
        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess();
    }

    private void deleteCategoryAndBrand(Integer id){

        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByPrimaryKey(example);

    }

}