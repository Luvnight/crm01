package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.mapper.SaleChanceMapper;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 批量删除；业务校验
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断要删除的id是否为空
        AssertUtil.isTrue(null==ids || ids.length==0,"请选择需要删除的数据");
        //删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)<0,"营销机会数据删除失败！");
    }

    /**
     * 多条件分页查询营销机会 (BaseService 中有对应的方法)
     * @param query
     * @return
     */
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query){
        Map<String,Object> map = new HashMap<>();
        //分页处理
        PageHelper.startPage(query.getPage(),query.getLimit());
        //
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        //返回
        return map;
    }


    /**
     * 营销机会数据添加
     * 1.参数校验
     * customerName:非空
     * linkMan:非空
     * linkPhone:非空 11位手机号
     * 2.设置相关参数默认值
     * state:默认未分配 如果选择分配人 state 为已分配
     * assignTime:如果 如果选择分配人 时间为当前系统时间
     * devResult:默认未开发 如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功
     3-开发失败
     * isValid:默认有效数据(1-有效 0-无效)
     * createDate updateDate:默认当前系统时间
     * 3.执行添加 判断结果
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //1.参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //2.设置相关参数默认值
        //未选择分配人
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);   //分配状态
            saleChance.setDevResult(0);   //开发状态
        }
        //选择分配人
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);   //分配状态
            saleChance.setDevResult(1);  //开发状态
            saleChance.setAssignTime(new Date());
        }
        //设定默认值 state,devResult(0--未开发，1--开发中，2--开发成功，3--开发失败)
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        saleChance.setIsValid(1);   //是否有效
        //是否添加成功
        AssertUtil.isTrue(insertSelective(saleChance)<1,"数据添加失败！");

    }


    /**
     * 营销机会数据更新
     *  1.参数校验
     *      id:记录必须存在
     *      customerName:非空
     *      linkMan:非空
     *      linkPhone:非空，11位手机号
     *  2. 设置相关参数值
     *      updateDate:系统当前时间
     *          原始记录 未分配 修改后改为已分配(由分配人决定)
     *              state 0->1
     *              assginTime 系统当前时间
     *              devResult 0-->1
     *          原始记录 已分配 修改后 为未分配
     *              state 0->1
     *              assginTime 系统当前时间
     *              devResult 0-->1
     *  3.执行更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)  //事物
    public void updateSaleChance(SaleChance saleChance){
        //1.参数校验
        //通过id查询记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断查询的记录是否为空
        AssertUtil.isTrue(null==temp,"待更新记录不存在");
        // 校验基础参数
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        // 2. 设置相关参数值
        // 先更新updateDate:系统当前时间
        saleChance.setUpdateDate(new Date());
        //判断是否有分配人
        if(StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            //原始纪录未分配：进行修改
            saleChance.setState(1);
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(1);  //开发中
        }else if(StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            //原始纪录已经分配：修改后改为未分配
            saleChance.setAssignMan("");
            saleChance.setState(0);
            saleChance.setAssignTime(null);
            saleChance.setDevResult(0);  //未开发
        }/*else if(StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setAssignTime(new Date());
        }*/

        // 3.执行更新 判断结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"营销机会数据更新失败！");
    }



    /**
     * 基本参数校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    public void checkParams(String customerName, String linkMan, String linkPhone){
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入手机号！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号格式不正确！");
    }


}
