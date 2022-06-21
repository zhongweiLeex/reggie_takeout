package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * description 分配管理
 *
 * @author Administrator
 * @date 2022/6/20-16:20
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /***
     * description: save 新增分类
     * @param category 前端传入的JSON字符串
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 17:00
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /***
     * description: 分页方法
     * @param page description
     * @param pageSize description
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page < com.itheima.reggie.entity.Category>>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 20:00
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * description: 根据ids 删除分类
     * @param ids description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 19:49
     */
    @DeleteMapping
    //因为前端只需要一个 String （code） 所以只需要返回string就好了
    public R<String> delete(Long ids){//是通过 URL 传递的 不需要RequestBody
        log.info("删除分类,{}",ids);

       //需要完善， 如果关联了菜品 ， 则无法删除 需要查询需要删除的分类是否已经关联了菜品
       categoryService.remove(ids);//调用的时 CategoryServiceImpl中的remove方法
       return R.success("分类信息删除成功");
    }

    /***
     * description: 根据ID修改分类信息
     * @param category description
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 20:56
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息");
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
}
