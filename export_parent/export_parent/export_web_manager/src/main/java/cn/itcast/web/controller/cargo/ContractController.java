package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController{

    // 注入dubbo的服务对象
    // import com.alibaba.dubbo.config.annotation.Reference;
    @Reference
    private ContractService contractService;

    /**
     * 1. 分页查询
     * @param pageNum  当前页，默认1
     * @param pageSize 页大小，默认5
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize){
        // 构造条件对象
        ContractExample example = new ContractExample();
        // 根据创建时间降序查询
        example.setOrderByClause("create_time desc");
        // 构造条件 - 企业id
        ContractExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        /**
         * 购销合同列表，要做细粒度权限控制。不同用户查看不同的列表数据
         * 用户等级：
         *      0-saas管理员
         *      1-企业管理员
         *      2-管理所有下属部门和人员
         *      3-管理本部门
         *      4-普通员工
         */
        //A. 获取登陆用户对象
        User user = getLoginUser();
        //B. 根据用户登陆判断
        if (user.getDegree() == 4){
            //C. 普通用户。只能查看自己创建的购销合同。查询条件是：登陆用户id
            // SQL: SELECT * FROM co_contract WHERE create_by='登陆用户id'
            criteria.andCreateByEqualTo(user.getId());
        }
        else if (user.getDegree() == 3){
            //D. 部门管理者，可以看到本部门员工创建的购销合同
            //SELECT * FROM co_contract WHERE create_dept='登陆用户的部门id'
            criteria.andCreateDeptEqualTo(user.getDeptId());
        }
        else if (user.getDegree() == 2){
            //D. 管理本部门和下属部门。查看本部门以及下属子部门创建的购销合同。
            //SELECT * FROM co_contract WHERE FIND_IN_SET('100',getDeptChild('100'))
            //根据登陆用户的部门id查询本部门及子部门的员工创建的购销合同。
            PageInfo<Contract> pageInfo =
                    contractService.findByDeptId(user.getDeptId(),pageNum,pageSize);
            // 返回
            ModelAndView mv = new ModelAndView();
            mv.addObject("pageInfo",pageInfo);
            mv.setViewName("cargo/contract/contract-list");
            return mv;
        }

        // 分页查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example,pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("cargo/contract/contract-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "cargo/contract/contract-add";
    }

    /**
     * 3. 添加或者修改
     */
    @RequestMapping("/edit")
    public String edit(Contract contract){
        // 设置购销合同所属企业信息
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(contract.getId())){
            // 因为后期购销合同列表要做细粒度权限控制，根据用户等级显示不同的列表数据。所以这里要记录创建人所属部门。
            contract.setCreateDept(getLoginUser().getDeptId());
            contract.setCreateBy(getLoginUser().getId());
            // 添加
            contractService.save(contract);
        }
        else {
            // 修改
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        Contract contract = contractService.findById(id);
        request.setAttribute("contract",contract);
        return "cargo/contract/contract-update";
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        contractService.delete(id);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 查看购销合同
     */
    @RequestMapping("/toView")
    public String toView(String id){
        Contract contract = contractService.findById(id);
        request.setAttribute("contract",contract);
        return "cargo/contract/contract-view";
    }

    /**
     * 修改购销合同状态 - 提交 1
     */
    @RequestMapping("/submit")
    public String submit(String id){
        Contract contract = new Contract();
        contract.setId(id);
        // 提交，设置状态为1，表示已上报
        contract.setState(1);
        // 修改
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 修改购销合同状态 - 取消 0
     */
    @RequestMapping("/cancel")
    public String cancel(String id){
        Contract contract = new Contract();
        contract.setId(id);
        // 提交，设置状态为1，表示已上报
        contract.setState(0);
        // 修改
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

}
