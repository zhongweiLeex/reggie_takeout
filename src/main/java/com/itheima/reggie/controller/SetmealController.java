package com.itheima.reggie.controller;

import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.impl.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description 操作的主表还是 setmeal
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

}
