package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description 检查用户是否已经完成登录
 * <p>
 *     使用过滤器方式
 * </p>
 *
 * @author Administrator
 * @date 2022/6/19-18:57
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*") //所有请求都过滤
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*
        * 1.获取本次请求URI
        * 2. 判断本次请求是否需要处理
        * 3. 如果不需要， 则直接放行
        * 4. 判断登录状态， 判断是否登录， 如果已经登录 则放行
        * 5. 如果没有登录则， 通过输出流 向客户端页面响应数据
        * */
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //不做拦截处理的URI
        String[] outURIs = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        //2.判断本次请求是否需要处理
        boolean noNeedHandle = check(outURIs,requestURI);

        //3. 如果不需要处理， 直接放行
        if(noNeedHandle){
            log.info("本次对于 {} 不需要处理，直接放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4. 判断是否用户已经登录， 如果已经登录 则直接放行
        Object employeeID = request.getSession().getAttribute("employee");
        BaseContext.setCurrentId((Long) employeeID);//设置用户ID 存储到 ThreadLocal中
        if(employeeID!=null){
            log.info("用户已登录，用户ID为{}",employeeID);
            filterChain.doFilter(request, response);
            return;
        }
        //5. 用户未登录，则通过输出流方式响应客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /***
     * description: 判断当前的URI是否处于 排除是排除在外的URI
     * @param outURIs 排除在拦截之外的URI
     * @param requestURI 浏览器端发来的请求
     * @return boolean true - 此请求URI 不需要排除在过滤器之外
     * @throws
     * @author zhongweileex
     * @date: 2022/6/19 - 19:20
     */
    private boolean check(String[] outURIs, String requestURI) {
        for(String outURI : outURIs){
            if(PATH_MATCHER.match(outURI,requestURI)){
                return true;
            }
        }
        return false;
    }
}
