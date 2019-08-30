package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {

    // 注入dubbo的服务对象
    @Reference
    private ContractService contractService;
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;

    /**
     * 1. 合同管理，只显示状态为1的购销合同
     * 功能入口：合同管理
     * 请求地址：http://localhost:8080/cargo/export/contractList.do
     * 后台处理： 根据状态查询购销合同
     * 响应地址：/WEB-INF/pages/cargo/export/export-contractList.jsp
     */
    @RequestMapping("/contractList")
    public ModelAndView contractList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        // 构造条件对象
        ContractExample example = new ContractExample();
        // 根据创建时间降序查询
        example.setOrderByClause("create_time desc");
        // 构造条件 - 企业id
        ContractExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        // 根据状态查询: 只显示状态为1的购销合同
        criteria.andStateEqualTo(1);

        // 分页查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example, pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("cargo/export/export-contractList");
        return mv;
    }

    /**
     * 2. 报运单列表
     * 请求地址：http://localhost:8080/cargo/export/list.do
     * 响应地址：/WEB-INF/pages/cargo/export/export-list.jsp
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        // 构造条件对象
        ExportExample example = new ExportExample();
        // 根据创建时间降序查询
        example.setOrderByClause("create_time desc");
        // 构造条件 - 企业id
        ExportExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        // 分页查询
        PageInfo<Export> pageInfo =
                exportService.findByPage(example, pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("cargo/export/export-list");
        return mv;
    }

    /**
     * 3. 进入报运单的添加页面
     * 功能入口： 合同管理点击报运
     * 请求地址： http://localhost:8080/cargo/export/toExport.do
     * 请求参数： 多个购销合同的id，  id=1&id=2
     * 响应地址： /WEB-INF/pages/cargo/export/export-toExport.jsp
     */
    @RequestMapping("/toExport")
    public String toExport(String id){
        // 保存购销合同id
        request.setAttribute("id",id);
        return "cargo/export/export-toExport";
    }

    /**
     * 4. 报运单添加或者修改方法
     */
    @RequestMapping("/edit")
    public String edit(Export export){
        // 设置购销合同所属企业信息
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(export.getId())){
            // 添加
            exportService.save(export);
        }
        else {
            // 修改
            exportService.update(export);
        }
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 4. 报运单修改（1）进入修改页面
     * 请求地址：http://localhost:8080/cargo/export/toUpdate.do
     * 请求参数：报运单id   如：id=1
     * 响应地址：cargo/export/export-update
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //4.1 根据报运单id查询报运单
        Export export = exportService.findById(id);
        request.setAttribute("export",export);

        //4.2 根据报运单id查询商品
        //4.2.1 构造条件： exportId
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        //4.2.2 根据报运单id查询
        List<ExportProduct> exportProductList =
                exportProductService.findAll(epExample);
        //4.2.3 保存报运商品
        request.setAttribute("eps",exportProductList);
        return "cargo/export/export-update";
    }

    /**
     * 出口报运单修改状态（1）提交， 把状态改为1，表示已上报
     * 请求地址：http://localhost:8080/cargo/export/submit.do?id=1
     */
    @RequestMapping("/submit")
    public String submit(String id){
        // 创建报运单对象
        Export export = new Export();
        // 设置报运单的id，作为修改条件
        export.setId(id);
        // 设置报运单的状态
        export.setState(1);
        // 修改
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }
    /**
     * 出口报运单修改状态（2）取消， 把状态改为0，表示草稿
     * 请求地址：http://localhost:8080/cargo/export/cancel.do?id=5
     */
    @RequestMapping("/cancel")
    public String cancel(String id){
        // 创建报运单对象
        Export export = new Export();
        // 设置报运单的id，作为修改条件
        export.setId(id);
        // 设置报运单的状态
        export.setState(0);
        // 修改
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 电子报运, 远程访问海关报运平台
     */
    @RequestMapping("/exportE")
    public String exportE(String id){
        //1. 根据列表选中的报运单id查询
        Export export = exportService.findById(id);

        //2. 创建对象，封装webservice请求数据
        ExportVo exportVo = new ExportVo();
        //2.1 封装报运单
        BeanUtils.copyProperties(export,exportVo);
        //2.2 设置报运单id
        exportVo.setExportId(id);

        //2.3 封装报运的商品
        List<ExportProductVo> products = exportVo.getProducts();
        //2.3.1 根据报运单id查询报运单下的所有商品
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> epList = exportProductService.findAll(epExample);
        //2.3.2 把报运的商品（epList）拷贝到报运单中(products)
        if (epList != null && epList.size()>0){
            for (ExportProduct exportProduct : epList) {
                //A. 创建报运单的商品的vo对象
                ExportProductVo epVo = new ExportProductVo();
                //B. 对象拷贝
                BeanUtils.copyProperties(exportProduct,epVo);
                //C. 设置参数
                epVo.setExportProductId(exportProduct.getId());
                epVo.setExportId(id);
                //D. 对象添加到集合
                products.add(epVo);
            }
        }
        //exportVo.setProducts(products);


        // 3. 远程访问海关平台（1）保存报运结果到海关平台数据库中
        WebClient.create("http://localhost:9001/ws/export/user")
                .post(exportVo);

        // 4. 远程访问海关平台（2）查询报运结果
        ExportResult exportResult = WebClient.create("http://localhost:9001/ws/export/user/" + id)
                .get(ExportResult.class);

        // 5. 根据报运结果，修改后台系统的数据库（报运状态，商品交税金额）
        exportService.updateExport(exportResult);

        // 6. 返回到报运列表
        return "redirect:/cargo/export/list.do";
    }
}