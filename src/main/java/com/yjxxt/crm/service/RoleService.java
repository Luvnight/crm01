package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.controller.RoleController;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询角色列表
     * @return
     */
    public List<Map<String,Object>> queryAllSoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名!");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp,"该角色已存在!");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(role)<1,"角色记录添加失败!");
    }

    /**
     * 更新角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        AssertUtil.isTrue(null==role.getId()||null==selectByPrimaryKey(role.getId()),"待修改的记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名!");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(role.getId())),"该角色已存在!");
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"角色记录更新失败!");
    }

    /**
     * 角色删除
     * @param roleId
     */
    public void deleteRole(Integer roleId){
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null == roleId || null == temp,"待删除的记录不存在!");
        temp.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"角色记录删除失败!");
    }

    /**
     * 权限记录的添加
     * @param mids
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mids, Integer roleId) {
        /**
        * 核心表-t_permission t_role(校验角色存在)
        * 如果角色存在原始权限 删除角色原始权限
        * 然后添加角色新的权限 批量添加权限记录到t_permission
         */
        Role temp =selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId||null==temp,"待授权的角色不存在!");
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByRoleId(roleId)<count,"权限分配失败!");
        }
        if(null !=mids && mids.length>0){
            List<Permission> permissions=new ArrayList<Permission>();
            for (Integer mid : mids) {
                Permission permission=new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissions.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }


}
