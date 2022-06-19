package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * description ： 实现查询等功能
 * <p> 继承 ServiceImpl
 * <p>
 *     ServiceImpl 内部实现了 IService接口
 *
 * </p>
 *
 * <p>
 *     ServiceImpl 内部仍然是对 BaseMapper 的封装
 *
 *
 *
 * @author Administrator
 * @date 2022/6/19-13:32
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
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
