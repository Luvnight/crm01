package com.yjxxt.crm.exceptions;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 全局异常统一处理
 *      控制层的方法返回的内容两种情况
 *          1. 视图:视图异常
 *          2. Json:方法执行错误 返回错误json信息
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * 方法返回值类型
     *      视图
     *      JSON
     *  如何判断方法的返回类型：
     *      如果方法级别配置了 @ResponseBody 注解，表示方法返回的是JSON；
     *      反之，返回的是视图页面
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //加入未登录异常判断；如果是未登录异常，则先执行相关的拦截操作
        if(ex instanceof NoLoginException){
            //如果捕获的是未登录异常，则重定向到登录页面
            ModelAndView mav = new ModelAndView("redirect:/index");
            return mav;
        }

        // 设置默认异常处理；其他全局的异常处理
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("code",400);
        mav.addObject("msg","系统异常，请稍后再试...");

        //判断 HandlerMethod
        if(handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            // 获取方法上的 ResponseBody 注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断 ResponseBody 注解是否存在 (如果不存在，表示返回的是视图;如果存在，表示返回的是JSON)
            if(null==responseBody){
                /**
                 * 方法返回视图
                 */
                if (ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    mav.addObject("code", pe.getCode());
                    mav.addObject("msg", pe.getMsg());
                }
                return mav;
            }else{
                /**
                 * 方法上返回JSON
                 */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                //如果捕获的是自定义异常
                if(ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                response.setContentType("application/json;charset=utf-8");
                //获取输出流
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    // 将对象转换成JSON格式，通过输出流输出 响应给请求的前台
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }
        return mav;
    }

}
