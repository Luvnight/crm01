package com.yjxxt.crm.interceptors;

import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  非法访问拦截
 */
public class NoLoginInterceptors extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Cookie中的用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 判断用户ID是否不为空，且数据库中存在对应的用户记录
        if(null==userId || null==userService.selectByPrimaryKey(userId)){
            //如果不存在，抛出异常
            throw new NoLoginException();
        }
        return true;
    }
}
