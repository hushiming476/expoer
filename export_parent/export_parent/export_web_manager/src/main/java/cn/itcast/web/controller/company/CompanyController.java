package cn.itcast.web.controller.company;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Reference
    private CompanyService companyService;

    /**
     * 1. 企业列表
     */
    @RequestMapping("/list")
    public ModelAndView list(){
        //1.1 调用service
        List<Company> list = companyService.findAll();
        //1.2 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("list",list);
        mv.setViewName("company/company-list");
        return mv;
    }

    /**
     * 2. 测试
     * 请求路径： http://localhost:8080/system/company/save.do?birth=1998-09-09
     * 后台方法： public String save(Date birth){}
     * 运行结果： HTTP Status 400 – Bad Request
     * 问题分析： SpringMVC不能把String--->Date
     * 如何解决：
     *          1. 写一个转换器类，实现Converter接口
     *          2. 配置springmvc.xml, 告诉springmvc我们自定义的转换器类。
     *             ConversionServiceFactoryBean
     */
    @RequestMapping(value = "/save",name = "保存测试")
    public String save(Date birth){
        int i = 1/0;
        System.out.println(birth);
        return "success";
    }

    /**
     * 3. 进入添加页面
     * 提交地址：http://localhost:8080/company/toAdd.do
     * 响应地址：/WEB-INF/pages/company/company-add.jsp
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "company/company-add";
    }

    /**
     * 4. 添加或修改
     * 提交地址：http://localhost:8080/company/edit.do
     * 提交参数：name,licenseId,city...
     * 响应地址：（重定向到列表）
     */
    @RequestMapping("/edit.do")
    public String edit(Company company){
        //4.1 根据id判断，如果id为NULL说明式是添加；否则为修改
        if (StringUtils.isEmpty(company.getId())){
            // 添加
            companyService.save(company);
        }
        else {
            // 修改
            companyService.update(company);
        }
        return "redirect:/company/list.do";
    }

    /**
     * 5. 进入修改页面
     * 提交地址：http://localhost:8080/company/toUpdate.do
     * 提交参数：id
     * 响应地址：/WEB-INF/pages/company/company-update.jsp  回显数据
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        //5.1 根据id查询企业
        Company company = companyService.findById(id);
        //5.2 返回
        ModelAndView mv = new ModelAndView();
        //a. 存储数据到request域
        mv.addObject("company",company);//request.setAttribute(key,value);
        //b. 设置跳转的页面地址
        mv.setViewName("company/company-update");
        return mv;
    }

    /**
     * 6. 删除
     * 提交地址：http://localhost:8080/delete/toUpdate.do
     * 提交参数：id
     * 响应地址：(重定向到列表)
     */
    @RequestMapping("/delete")
    public String delete(String id){
        companyService.delete(id);
        return "redirect:/company/list.do";
    }
}
