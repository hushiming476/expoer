package cn.itcast.web;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApplyController {

    // 注入dubbo提供的服务对象
    @Reference
    private CompanyService companyService;

    /**
     * 企业入驻（添加企业信息）
     */
    @RequestMapping("/apply")
    @ResponseBody
    public String apply(Company company){
        try {
            companyService.save(company);
            return "1"; // 保存成功
        } catch (Exception e) {
            e.printStackTrace();
            return "2"; // 保存失败
        }
    }
}
