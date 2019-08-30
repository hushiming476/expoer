package cn.itcast.web.stat;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.stat.service.StatService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController {

    // 注入统计分析service
    @Reference
    private StatService statService;


    /**
     * 统计分析，进入统计分析页面
     * 访问地址：
     *    http://localhost:8080/stat/toCharts.do?chartsType=factory
     *    http://localhost:8080/stat/toCharts.do?chartsType=online
     *    http://localhost:8080/stat/toCharts.do?chartsType=sell
     * 进入页面：
     *    /WEB-INF/pages/stat/stat-factory.jsp
     *    /WEB-INF/pages/stat/stat-online.jsp
     *    /WEB-INF/pages/stat/stat-sell.jsp
     */
    @RequestMapping("/toCharts")
    public String toCharts(String chartsType){
        return "stat/stat-" + chartsType;
    }

    /**
     * 需求1：根据生产厂家统计
     * 页面请求：ajax请求
     */
    @RequestMapping("/getFactoryData")
    @ResponseBody  // 自动把方法返回结果转换为json格式
    public List<Map<String, Object>> getFactoryData(){
        List<Map<String, Object>> list = statService.getFactoryData(getLoginCompanyId());
        return list;
    }

    /**
     * 需求2：产品销售排行
     * 页面请求：ajax请求
     */
    @RequestMapping("/getProductData")
    @ResponseBody  // 自动把方法返回结果转换为json格式
    public List<Map<String, Object>> getProductData(){
        List<Map<String, Object>> list = statService.getProductData(getLoginCompanyId());
        return list;
    }

    /**
     * 需求2：系统访问压力图
     * 页面请求：ajax请求
     */
    @RequestMapping("/getOnlineData")
    @ResponseBody  // 自动把方法返回结果转换为json格式
    public List<Map<String, Object>> getOnlineData(){
        List<Map<String, Object>> list = statService.getOnlineData(getLoginCompanyId());
        return list;
    }

}