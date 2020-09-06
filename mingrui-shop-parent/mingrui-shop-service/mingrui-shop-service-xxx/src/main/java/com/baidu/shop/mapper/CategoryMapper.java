package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<CategoryEntity>,SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "select c.id,c.name from tb_category c where c.id in(select t.category_id from tb_category_brand t where t.brand_id=#{brandId})")
    List<CategoryEntity> getByBrandId(Integer brandId);

//    @Select(value = "select c.id,c.name from tb_category c where c.id in (select cb.category_id from tb_category_brand cb where cb.brand_id=#{brandId})")
//    List<CategoryEntity> getByBrandId(Integer brandId);

    @Select(value = "select count(1) from tb_category_brand  where category_id = #{id}")
    Integer  getByCategoryId(Integer id);

    @Select(value = "select count(1) from tb_spec_group  where cid = #{id}")
    Integer getSepcGroup(Integer id);

}
