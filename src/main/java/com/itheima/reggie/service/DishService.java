package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品， 同时插入菜品对应的口味数据 需要操作两张表格， dish dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id 查询菜品信息和对应口味信息
    DishDto getByIdWithFlavor(Long id);

    void updateByIdWithFlavor(DishDto dishDto);
}
