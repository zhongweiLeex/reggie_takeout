package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description TODO
 *
 * @author Administrator
 * @date 2022/6/20-20:06
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    //用来操作setmeal_dish 关联关系表
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐， 同时需要保存套餐 与 菜品的关联关系
     * @param setmealDto DTO类
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息 ， 操作 Setmeal 套餐表 执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek((item) -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());

        //保存套餐和菜品的关联信息， 操作setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * description: 删除套餐 ， 同时删除套餐和菜品的关联数据 <br> 只有停售的套餐才能删除
     * @param ids List 需要删除的setmeal id
     * @return void
     * @throws CustomException
     * @author zhongweileex
     * @date: 2022/6/27 - 20:45
     */
    @Override
    @Transactional //同时操作两张表 需要添加事务注解
    public void removeWithDish(List<Long> ids) {
/*
        查询套餐状态， 确定是否可以删除
        如果可以删除
             先删除套餐表中的数据
            然后删除关系表中的数据
        若不能删除，直接抛出异常

*/
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        //想要删除的套餐正在售卖中， 则不能删除
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(setmealLambdaQueryWrapper);
        if(count > 0){
            throw new CustomException("套餐正在售卖中， 不能删除");
        }
        //删除setmeal中的数据
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (ids)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除 setmeal_dish 表中数据
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
