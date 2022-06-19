package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

/**
 * description EmployeeController 控制层
 *
 * @author Administrator
 * @date 2022/6/19-13:57
 */
@Slf4j
@RestController
/*
@RestController是@ResponseBody和@Controller的组合注解
@RestController 使用的效果是将方法返回的对象直接在浏览器上展示成 json 格式
    之前使用 @Controller + @ResponseBody
    @Controller 表示此类为一个控制器类
    @ResponseBody 注解表示方法返回值直接以指定格式写入Http Response Body中，而不是解析为转跳路径

*/

@RequestMapping(value = "/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /***
     * description: 员工登录 <p>此处可以使用 token 令牌管理 权限</p>
     *
     * @param request 请求
     * @param employee 方法返回值 以指定格式写入 Http Response Body中
     * @return com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 16:14
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1. 根据页面提交的密码进行 md5加密处理
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //2. 根据页面提交的用户名 username 查询数据库
        //使用mybatisplus中的 QueryWrapper
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        //调用Mybatisplus的 IService接口中的查询方法 , 用户名是在数据库表中是唯一的
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        //3.如果没有查询到对应的用户名称， 则直接返回失败结果
        if(emp == null) return R.error("登录失败");

        //4. 密码对比， 如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败");
        }

        //5. 查看员工状态
        if(emp.getStatus()==0){
            return R.error("账号禁用");
        }

        //6. 登录成功
        //登录成功，将员工Id存放到session 并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出并且清理当前当前保存登陆员工的sessionID
     * @param request request请求
     * @return "R"
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }
}
