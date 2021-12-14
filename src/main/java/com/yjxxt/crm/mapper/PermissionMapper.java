package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    int countPermissionByRoleId(Integer roleId);


    int deletePermissionsByRoleId(Integer roleId);

    List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);


    List<String> queryUserHasRolesHasPermissions(Integer userId);

    int countPermissionsByModuleId(Integer mid);

    Integer deletePermissionsByModuleId(Integer mid);
}