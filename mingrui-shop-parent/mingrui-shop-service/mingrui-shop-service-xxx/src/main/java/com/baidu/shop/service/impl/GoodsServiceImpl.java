package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<Map<String, Object>> getSpuInfo(SpuDTO spuDTO) {

        PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        Example example = new Example(SpuEntity.class);

        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(spuDTO)) {

            if(!StringUtils.isEmpty(spuDTO.getTitle()))
                criteria.andLike("title","%" + spuDTO.getTitle() + "%");

            if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
                criteria.andEqualTo("saleable",spuDTO.getSaleable());

            if (!StringUtils.isEmpty(spuDTO.getSort()))
                example.setOrderByClause(spuDTO.getOrderByClause());
        }

        List<SpuEntity> list = spuMapper.selectByExample(example);

        List<SpuDTO> dtos = new ArrayList<>();

        list.stream().forEach(spu -> {

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spu.getBrandId());
            String categoryNames = categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream()
                    .map(category -> category.getName())
                    .collect(Collectors.joining("/"));

            SpuDTO dto = BaiduBeanUtil.copyProperties(spu, SpuDTO.class);
            dto.setBrandName(brandEntity.getName());

            dto.setCategoryName(categoryNames);

            dtos.add(dto);
        });

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        long total = pageInfo.getTotal();

        Map<String, Object> map = new HashMap<>();
        map.put("list",dtos);
        map.put("total",total);

        return this.setResultSuccess(map);
    }

}
