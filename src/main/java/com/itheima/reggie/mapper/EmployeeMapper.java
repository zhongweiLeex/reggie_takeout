package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper类 继承了 Mybatisplus 的BaseMapper 接口 <p>
 *     具有常见的增删改查方法
 */
@Mapper
/*
1. 需要将mapper 定义成一个接口 标识将此dao接口 交给Spring 管理
2. 不用写Mapper映射文件 xml文件
3. 为此dao接口生成一个实现类，让别的类引用
 */
public interface EmployeeMapper extends BaseMapper<Employee> {


}
