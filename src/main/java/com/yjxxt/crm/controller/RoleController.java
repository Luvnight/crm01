package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    /**
     * 查询角色列表
     *
     * @return
     */
    @RequestMapping("/queryAllRoles")
    @ResponseBody
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleService.queryAllSoles(userId);
    }

    /**
     * 角色页面跳转
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "role/role";
    }

    /**
     * 分页列表
     * @param roleQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    @RequiredPermission(code = "60")
    public Map<String, Object> userList(RoleQuery roleQuery) {
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 添加与修改页面的跳转
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer id, Model model){
        if(null !=id){
            model.addAttribute("role",roleService.selectByPrimaryKey(id));
        }
        return "role/add_update";
    }

    /**
     * 角色添加
     * @param role
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.saveRole(role);
        return success("角色记录添加成功");
    }

    /**
     * 角色修改
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色记录更新成功");
    }

    /**
     * 角色删除
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色记录删除成功");
    }

    /**
     * 授权页面跳转
     * @param roleId
     * @param model
     * @return
     */
    @RequestMapping("toRoleGrantPage")
    public String toAddGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 权限添加
     * @param mids
     * @param roleId
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids,roleId);
        return success("权限添加成功");
    }




}
