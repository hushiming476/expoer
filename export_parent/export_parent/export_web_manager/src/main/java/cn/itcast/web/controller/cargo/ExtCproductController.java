package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {

    // 注入dubbo的服务对象: 附件的service
    @Reference
    private ExtCproductService extCproductService;
    // 货物的service
    @Reference
    private ContractProductService contractProductService;
    // 工厂的service
    @Reference
    private FactoryService factoryService;

    /**
     * 附件的添加与列表展示
     * 功能入口： 货物列表，点击附件
     * 请求地址：http://localhost:8080/cargo/extCproduct/list.do
     * 请求参数：contractId=1&contractProductId=2
     * 响应页面：WEB-INF/pages/cargo/extc/extc-list.jsp
     */
    @RequestMapping("/list")
    public String list(String contractId, String contractProductId,
                       @RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "5") int pageSize) {

        //1.1 查询并保存货物的工厂
        //A.构造条件
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        //B.条件查询
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        //C.保存
        request.setAttribute("factoryList", factoryList);


        //1.2 查询货物下的附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        // 查询条件：货物id
        extCproductExample.createCriteria()
                .andContractProductIdEqualTo(contractProductId);
        PageInfo<ExtCproduct> pageInfo =
                extCproductService.findByPage(extCproductExample, pageNum, pageSize);
        // 保存附件数据
        request.setAttribute("pageInfo", pageInfo);

        //1.3 存储数据
        request.setAttribute("contractId", contractId);
        request.setAttribute("contractProductId", contractProductId);

        //1.4 进入货物列表和添加页面
        return "cargo/extc/extc-list";
    }

    /**
     * 附件的添加或修改
     */
    @RequestMapping("/edit")
    public String edit(ExtCproduct extCproduct) throws Exception {
        // 设置购销合同货物所属企业信息
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(extCproduct.getId())) {
            extCproductService.save(extCproduct);
        } else {
            extCproductService.update(extCproduct);
        }
        // 添加修改成功还回到附件列表页面。contractId=1&contractProductId=2
        return "redirect:/cargo/extCproduct/list.do?contractId=" +
                extCproduct.getContractId() + "&contractProductId=" + extCproduct.getContractProductId();
    }

    /**
     * 进入附件修改页面
     * 请求地址：http://localhost:8080/cargo/extCproduct/toUpdate.do
     * 请求参数：id=2&contractId=2&contractProductId=3
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id, String contractId, String contractProductId) {
        //1 查询货物的工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);

        //2 根据附件id查询货物附件并保存
        ExtCproduct extCproduct = extCproductService.findById(id);
        request.setAttribute("extCproduct", extCproduct);

        //3. 保存货物id、购销合同id
        request.setAttribute("contractProductId", contractProductId);
        request.setAttribute("contractId", contractId);

        //4. 进入附件修改页面
        return "cargo/extc/extc-update";
    }

    /**
     * 删除附件
     * 请求地址：http://localhost:8080/cargo/extCproduct/delete.do
     * 请求参数：id=1contractId=2&contractProductId=3
     */
    @RequestMapping("/delete")
    public String delete(String id, String contractId, String contractProductId) {
        // 调用service删除
        extCproductService.delete(id);
        // 返回附件列表
        // 添加修改成功还回到附件列表页面。contractId=1&contractProductId=2
        return "redirect:/cargo/extCproduct/list.do?contractId=" + contractId +
                "&contractProductId=" + contractProductId;
    }
}