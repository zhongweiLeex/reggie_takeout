package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description 前端传过来的两个表格数据  菜品 + 菜品口味 <br>同时管理 菜品 和 菜品口味
 *
 * @author Administrator
 * @date 2022/6/21-11:01
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /***
     * description: 新增菜品
     * @param dishDto description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 16:25
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }
    /***
     * description: 菜品信息分页
     * @param page description
     * @param pageSize description
     * @param name description
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 15:10
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name){

       //构造分页构造器  mybatisplus 提供的分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);


        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lambdaQueryWrapper);

        //对象拷贝
        Page<DishDto> dishDtoPage = new Page<>();
        //recodes 表示 一个page中的所有记录 ， 所以这里先将元page中的recodes 中的信息排除
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //在原有的records上重新构造 DishDto 类的page records(根据 categoryId 添加 categoryName)
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //根据 categoryId 获取 categoryName 然后添加到 dishDto中 最后 通过stream方式添加到 新的page的records中
            Long categoryId = item.getCategoryId();//分类ID
            Category category = categoryService.getById(categoryId);//根据id查询分类对象
            String categoryName = category.getName();//查询分类名称
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /***
     * description: 修改页面回显 功能 <br>根据id 查询菜品信息 + 口味信息
     * @param id description
     * @return com.itheima.reggie.common.R<com.itheima.reggie.dto.DishDto>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 16:35
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        //需要查询两张表格
        DishDto dishDto = dishService.getByIdWithFlavor(id);//主要逻辑封装到 dishService中
        return R.success(dishDto);
    }

    /***
     * description: 修改菜品
     * @param dishDto description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 16:55
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateByIdWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /***
     * description: 根据条件查询对应菜品数据
     * @param dish description
     * @return com.itheima.reggie.common.R<java.util.List < com.itheima.reggie.entity.Dish>>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/27 - 16:38
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //构造查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);//查询状态为1 （在售状态）

        //构造排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

}

