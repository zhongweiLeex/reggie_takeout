package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.DishFlavor;

/**
 * 需要操作两张表  dish表 + dishFlavor表
 */
public interface DishFlavorService extends IService<DishFlavor> {
}
