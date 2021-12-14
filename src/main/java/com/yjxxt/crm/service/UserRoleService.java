package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserRoleMapper;

import javax.annotation.Resource;

public class UserRoleService extends BaseService<UserRole,Integer> {

    @Resource
    private UserRoleMapper userRoleMapper;
}
