package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理 <br>操作的主表还是 setmeal <br></br>如果需要操作关联关系表 则 同一在内部调用相关的service就行
 *
 * @author Administrator
 * @date 2022/6/21-19:38
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /***
     * description: 新增套餐
     * @param setmealDto description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/27 - 17:00
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * description: 套餐分页查询
     * @param page description
     * @param pageSize description
     * @param name description
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page < com.itheima.reggie.entity.Setmeal>>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/27 - 19:39
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //SetmealDto 中具有 categoryName 这个字段
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //查询条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //分页查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝 因为 Page<>中的records<T> 中的泛型不一样，所以需要排除在复制之外
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        //重新添加 categoryName 字段
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID 查询
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * description: 删除套餐
     * @param ids 地址传递过来的参数
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/27 - 20:41
     */
    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

}
