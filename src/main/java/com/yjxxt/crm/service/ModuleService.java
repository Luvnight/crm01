package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 所有的资源信息
     * @return
     */
    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }

    /**
     * 根据角色的id查询拥有多少资源信息
     * @param roleId
     * @return
     */
    public List<TreeDto> queryAllModules02(Integer roleId) {
        //获取所有的资源信息
        List<TreeDto> treeDtos=moduleMapper.selectModules();
        // 根据角色id 查询角色拥有的资源信息
        List<Integer> roleHasMids=permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        if(null !=roleHasMids && roleHasMids.size()>0){
            treeDtos.forEach(treeDto -> {
                if(roleHasMids.contains(treeDto.getId())){
                    // 说明当前角色 分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }


    public Map<String, Object> queryModules() {
        //准备数据
        Map<String,Object> map = new HashMap<>();
        //查询所有的资源
        List<Module> modules =moduleMapper.queryModules();
        //准备数据
        map.put("count",modules.size());
        map.put("data",modules);
        map.put("code",0);
        map.put("msg","success");
        return map;
    }

    /**
     * 资源记录添加
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module){
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入菜单名!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(),module.getModuleName()),"该层级下菜单重复!");
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单url值");
                    AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndUrl(module.getGrade(),module.getUrl()),"二级菜单url不可重复!");
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId ||
                    null==selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复!");
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(module)<1,"菜单添加失败!");
    }

    /**
     * 资源记录更新
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        AssertUtil.isTrue(null == module.getId() || null==
                selectByPrimaryKey(module.getId()),"待更新记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请指定菜单名称!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        Module temp =moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下菜单已存在!");
        }
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单url值");
                    temp =moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if(null !=temp){
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下url已存在!");
            }
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId ||
                    null==selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码!");
        temp =moduleMapper.queryModuleByOptValue(module.getOptValue());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"权限码已存在!");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(module)<1,"菜单更新失败!");
    }

    /**
     *
     * @param grade
     * @return
     */
    public List<Map<String, Object>> queryAllModulesByGrade(Integer grade) {
        return moduleMapper.queryAllModulesByGrade(grade);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid){
        Module temp =selectByPrimaryKey(mid);
        AssertUtil.isTrue(null == mid || null == temp,"待删除记录不存在!");
        /*** 如果存在子菜单 不允许删除 */
        int count = moduleMapper.countSubModuleByParentId(mid);
        AssertUtil.isTrue(count>0,"存在子菜单，不支持删除操作!");
        // 权限表
        count =permissionMapper.countPermissionsByModuleId(mid);
        if(count>0){ AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid) <count,"菜单删除失败!");
        }
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"菜单删除失败!");

    }

}
