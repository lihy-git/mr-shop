package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CategoryEntity
 * @Description: TODO
 * @Author lihongyang
 * @Date 2020/8/27
 * @Version V1.0
 **/
@ApiModel(value = "分类是实体类")
@Data
@Table(name = "tb_category")
public class CategoryEntity {

    @Id
    @ApiModelProperty(value = "分类主键", example = "1")
    @NotNull(message = "id不能为null", groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "主键",example = "1")
    @NotEmpty(message = "类目名称不能为null", groups = {MingruiOperation.Add.class, MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "父级分类",example = "1")
    @NotEmpty(message = "父级分类不能为null", groups = {MingruiOperation.Add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否是父级节点",example = "1")
    @NotEmpty(message = "是否是父级节点不能为null", groups = {MingruiOperation.Add.class})
    private Integer isParent;

    @ApiModelProperty(value = "排序",example = "1")
    @NotEmpty(message = "排序指数不能为null", groups = {MingruiOperation.Add.class})
    private Integer sort;

}
