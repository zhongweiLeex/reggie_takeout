package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper类 继承了 Mybatisplus 的BaseMapper 接口 <p>
 *     具有常见的增删改查方法
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
