package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description 因为前端中 需要操作 dish 的同时 操作 dishflavor 所以需要扩展service接口
 *
 * @author Administrator
 * @date 2022/6/20-20:05
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    //dish数据库
    @Autowired
    private DishFlavorService dishFlavorService;

    /***
     * description: saveWithFlavor 这个方法同时操作了两张表  dish  dishflavor <br>因为涉及多张表格，所以需要加入事务控制
     * @param dishDto 继承了Dish类的 数据传输类 ， 接收前端传来的封装的数据
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 14:55
     */
    @Transactional//涉及多个表格， 开启事务
    public void saveWithFlavor(DishDto dishDto){
        /*
            保存dish的基本信息到dish表中
            因为 dishDto 继承了 Dish 所以 该有的数据都有
        */
        this.save(dishDto);
        // 由于前端传来的JSON 没有封装 dishId 所以 需要手动添加
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将每个 口味中都添加 dishid 因为dishFlavor 表格中 都会有dishid 这个字段
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到 菜品口味表 dish_flavors
        dishFlavorService.saveBatch(flavors);
    }

    /***
     * description: 根据id查询dish信息和对应的口味信息
     * @param id description
     * @return com.itheima.reggie.dto.DishDto
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 16:39
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询dish 基本信息 从dish 表查询
        Dish dish = this.getById(id);

        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询 当前菜品对应的口味信息  从 dish_flavor 表查询
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);


        return dishDto;
    }

    @Override
    @Transactional
    public void updateByIdWithFlavor(DishDto dishDto) {
        //更新 dish表
        this.updateById(dishDto);

        //更新 flavor表


        //先删除菜品对应口味数据 --- dish_flavor表的delete操作
        //相当于 delete from dish_flavor where id = ?
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        //再添加当前提交过来的口味数据 --- dish_flavor表的insert操作
        //因为 前端传过来的数据中 没有封装 dishFlavor 的 dishId, 所以要手动添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId((dishDto.getId()));
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
