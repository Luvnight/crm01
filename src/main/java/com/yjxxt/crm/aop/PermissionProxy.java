package com.yjxxt.crm.aop;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.exceptions.NoAuthException;
import com.yjxxt.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Autowired
    private HttpSession session;

    @Around(value = "@annotation(com.yjxxt.crm.annotation.RequiredPermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //判断session中是否有资源权限
        List<String> permissions = (List<String>)session.getAttribute("permissions");
        if(null == permissions || permissions.size()==0){
            //无权限，抛出未登录异常
            throw new NoLoginException();
        }
        //判断是否有访问目标资源的权限码
        Object result =null;
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RequiredPermission requirePermission =
                methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //比对permissions中是否有方法上的权限码
        if(!(permissions.contains(requirePermission.code()))){
            throw new NoAuthException("无权限访问！");
        }
        result= pjp.proceed();
        return result;
    }

}
