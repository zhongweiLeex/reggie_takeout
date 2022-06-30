package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象 ： 介于 表示层 与 service层之间的 一个类， 用于 表示层与服务层之间的数据传输 <br> 因为 前面表示层传输过来的 flavors Dish类中是没有的 ，所以需要一个中间类来整合
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DishDto extends Dish {


    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
