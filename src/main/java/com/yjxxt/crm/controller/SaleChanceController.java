package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private UserService userService;

    /**
     * 进入营销机会管理页面
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "saleChance/sale_chance";
    }


    /**
     * 多条件查询
     * @param query
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> sayList(SaleChanceQuery query){
        return saleChanceService.querySaleChanceByParams(query);
    }

    /**
     * 营销机会数据的添加
     * @param req
     * @param saleChance
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest req, SaleChance saleChance){
        //通过cookie获取用户的userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //设置营销机会的创建人（当前登录用户）
        saleChance.setCreateMan(trueName);
        //添加营销机会的数据
        saleChanceService.addSaleChance(saleChance);
        //返回目标对象ResultInfo
        return success("添加成功！");
    }

    /**
     * 机会数据添加与更新页面视图转发
     *      id为空  添加操作
     *      id非空  修改操作
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        //如果id不为空，表示是修改操作，修改操作需要查询被修改的数据
        if(null!=id){
            //通过id查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //将数据存到作用域中
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){
        //更新营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功！");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //删除营销机会数据
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功！");
    }
}
