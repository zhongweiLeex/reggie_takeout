package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.JacksonObjectMapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.sun.crypto.provider.HmacMD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

    /***
     * description: 新增员工
     * @param employee 员工信息
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 20:20
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息，{}" ,employee.toString());
        //设置初始密码 123456 并且通过 md5 加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

/*
        使用mybatisplus 自动填充
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        获取当前用户登录ID
        Long empID = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);
*/

        //向数据库中保存这个新增员工
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /***
     * description: 员工信息分页查询，页面需要什么数据 ， 就要返回什么数据
     * @param page description
     * @param pageSize description
     * @param name description
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 21:46
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页条件
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件 where 子句相当于
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件 order by updateTime
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //起到分页功能
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    /***
     * description: <p>
     *     根据用户ID，更改用户信息，
     * </p>
     * <p>
     *     账号启用/禁用
     * </p>
     * <p>
     *     用户信息修改
     * </p>
     * @param request request请求
     * @param employee 从前端来的JSON数据
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 8:05
     */
    //putmapping 倾向于 更新信息 ， postmapping 倾向于添加信息
    //@RequestBody 主要用于接收前端传递给后端的JSON字符串的数据
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        /*
            Long empId = (Long) request.getSession().getAttribute("employee");
            employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(empId);
        */

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    /***
     * description: 根据ID查询员工信息
     * @param id pathVariable 表示路径变量， 此变量在请求路径中，且 此处的跟新修改， 在前端中复用了上面的 update方法
     * @return com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     * @throws
     * @author zhongweileex
     * @date: 2022/6/20 - 14:22
     */
    @GetMapping("/{id}")
    //PathVariable 表示路径变量， 表示此变量在请求路径中
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee == null){
            return R.error("没有查询到员工信息");
        }
        return R.success(employee);
    }
}
