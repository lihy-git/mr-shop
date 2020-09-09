package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author lihongyang
 * @Date 2020/9/4
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Autowired
    private BrandService brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;


    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage())
                && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        //构建条件查询
        Example example = new Example(SpuEntity.class);
        //构建查询条件
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        //排序
        if(ObjectUtil.isNotNull(spuDTO.getSort()))
            example.setOrderByClause(spuDTO.getOrderByClause());
        List<SpuEntity> list = spuMapper.selectByExample(example);
        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuEntity.getBrandId());
            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);
            if (ObjectUtil.isNotNull(brandInfo)) {
                PageInfo<BrandEntity> data = brandInfo.getData();
                List<BrandEntity> list1 = data.getList();
                if (!list1.isEmpty() && list1.size() == 1) {
                    spuDTO1.setBrandName(list1.get(0).getName());
                }
            }
            //分类名称
            String caterogyName = categoryMapper.selectByIdList(
                    Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
                    .stream().map(category -> category.getName())
                    .collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(caterogyName);
            return spuDTO1;
        }).collect(Collectors.toList());
        PageInfo<SpuEntity> info = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,info.getTotal() + "",spuDtoList);
    }

    @Override
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {

        Date date = new Date();

        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        //新增spu
        spuMapper.insertSelective(spuEntity);

        Integer spuId = spuEntity.getId();
        //新增spudetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuId);
        spuDetailMapper.insertSelective(spuDetailEntity);

        spuDTO.getSkus().stream().forEach(skuDTO -> {
            //新增sku!!!!!
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });

        return this.setResultSuccess();
    }

    //@Override
    //public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {

    //分页
//        if(ObjectUtil.isNotNull(spuDTO.getPage())
//                && ObjectUtil.isNotNull(spuDTO.getRows()))
//            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

    //构建条件查询
    //Example example = new Example(SpuEntity.class);
    //构建查询条件
//        Example.Criteria criteria = example.createCriteria();
//        if(StringUtil.isNotEmpty(spuDTO.getTitle()))
//            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
//        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
//            criteria.andEqualTo("saleable",spuDTO.getSaleable());

    //排序
//        if(ObjectUtil.isNotNull(spuDTO.getSort()))
//            example.setOrderByClause(spuDTO.getOrderByClause());
//
//        List<SpuEntity> list = spuMapper.selectByExample(example);

    //可以有优化的空间

//        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {
//            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

    //设置品牌名称
//            BrandDTO brandDTO = new BrandDTO();
//            brandDTO.setId(spuEntity.getBrandId());
//            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);
//
//            if (ObjectUtil.isNotNull(brandInfo)) {
//
//                PageInfo<BrandEntity> data = brandInfo.getData();
//                List<BrandEntity> list1 = data.getList();
//
//                if (!list1.isEmpty() && list1.size() == 1) {
//                    spuDTO1.setBrandName(list1.get(0).getName());
//                }
//            }

    //设置分类
    //通过cid1 cid2 cid3

//            List<CategoryEntity> categoryEntityList = categoryMapper.selectByIdList(Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()));
//            String caterogyName = categoryEntityList.stream().map(category -> category.getName()).collect(Collectors.joining("/"));

    //分类名称
//            String caterogyName = categoryMapper.selectByIdList(
//                    Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
//                    .stream().map(category -> category.getName())
//                    .collect(Collectors.joining("/"));
//
//            spuDTO1.setCategoryName(caterogyName);
//
//            return spuDTO1;
//        }).collect(Collectors.toList());

//        List<SpuDTO> spuDTOS = new ArrayList<>();
//        list.stream().forEach(spuEntity -> {
//            //通过品牌id查询品牌名称
//
//            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
//
//            BrandDTO brandDTO = new BrandDTO();
//            brandDTO.setId(spuEntity.getBrandId());
//
//            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);
//            if (ObjectUtil.isNotNull(brandInfo)) {
//
//                PageInfo<BrandEntity> data = brandInfo.getData();
//                List<BrandEntity> list1 = data.getList();
//
//                if(!list1.isEmpty() && list1.size() == 1){
//                    spuDTO1.setBrandName(list1.get(0).getName());
//                }
//            }
//        });

//        PageInfo<SpuDTO> info = new PageInfo<>(spuDtoList);
//
//        return this.setResultSuccess(info);
//    }

}
