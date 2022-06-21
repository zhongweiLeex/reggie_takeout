package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description TODO
 *
 * @author Administrator
 * @date 2022/6/20-16:27
 */
@Service
/*
 具体需要查看 ServiceImpl 的源码
      ServiceImpl 类签名为
      泛型参数：
      1.  M mapper类
      2.  T 实体类
      public class ServiceImpl<M extends BaseMapper<M>,T> implements IService<T>
 */
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //构造一个查询条件对象
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //查询到当前分类是否关联了菜品，如果已经关联，抛出业务异常
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int countOfDish = dishService.count(dishLambdaQueryWrapper);
        if(countOfDish>0){
//            log.info("已经关联菜品，无法删除，抛出异常");
            throw new CustomException("当前分类已经关联菜品，无法删除，抛出异常");
        }

        //构造一个查询条件对象
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询到当前分类是否关联了套餐，如果已经关联，抛出业务异常
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int countOfSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if(countOfSetmeal > 0){
            throw new CustomException("当前分类已经关联套餐，无法删除，抛出异常");
        }
        //上述两种查询，都没有关联， 则正常删除
        super.removeById(id);
    }
}
