package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    /**
     * 系统登录页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }
    // 系统界面欢迎页
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主页面；添加页面显示信息
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest req){
        // 通过工具类，从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        // 调用对应Service层的方法，通过userId主键查询用户对象
        User user = userService.selectByPrimaryKey(userId);
        // 将用户对象设置到request作用域中
        req.setAttribute("user", user);
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        //打印权限码
        /*for (String code:permissions) {
            System.out.println(code+"<<<<权限码");
        }*/

        req.getSession().setAttribute("permissions",permissions);
        //转发
        return "main";
    }
}
