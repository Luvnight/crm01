package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.LoginUserUtil;
import com.yjxxt.crm.utils.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {
        //响应结果信息
        ResultInfo resultInfo = new ResultInfo();
        //调用Service层的登录方法，得到返回的用户对象
        UserModel userModel = userService.userLogin(userName, userPwd);
        // 将返回的UserModel对象设置到 ResultInfo对象中
        resultInfo.setResult(userModel);
        //ResultInfo--->json
        return resultInfo;
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,
                                         String oldPassword, String newPassword,
                                         String confirmPassword) {
        //响应结果信息
        ResultInfo resultInfo = new ResultInfo();
        // 获取userId；从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        //ResultInfo--->json
        return resultInfo;
    }

    //主页面main跳转至修改密码password
    @RequestMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }

    //主页面main跳转至基本资料setting
    @RequestMapping("toSettingPage")
    public String toSettingPage(HttpServletRequest req) {
        //通过cookie获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user", user);
        return "user/setting";
    }

    @PostMapping("/setting")
    @ResponseBody
    public ResultInfo userUpdate(User user) {
        //响应结果信息
        ResultInfo resultInfo = new ResultInfo();
        //调用Service层的登录方法，得到返回的用户对象
        userService.updateByPrimaryKeySelective(user);
        //ResultInfo--->json
        return resultInfo;
    }

    /**
     * 查询所有的销售人员
     *
     * @return
     */
    @RequestMapping("/queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }


    /*
    ---------------day3----用户模块开发------------------------------------------------------
     */

    /**
     * 用户模块条件查询
     *
     * @param userQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }

    /**
     * 进入用户模块页面
     */
    @RequestMapping("/index")
    public String userIndex() {
        return "user/user";
    }

    /**
     * 用户添加
     *
     * @param user
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("用户添加成功！");
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.changeUser(user);
        return success("用户更新成功！");
    }

    /**
     * 添加与修改页面跳转
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model) {
        //判断id是否为空
        if (null != id) {
            model.addAttribute("user", userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }

    /**
     * 删除用户（批量删除）
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {
        userService.deleteUserByIds(ids);
        return success("用户记录删除成功");
    }


}
