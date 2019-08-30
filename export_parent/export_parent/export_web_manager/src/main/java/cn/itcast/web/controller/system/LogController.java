package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.SysLog;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.SysLogService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/log")
public class LogController extends BaseController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 1. 分页查询
     *
     * @param pageNum  当前页，默认1
     * @param pageSize 页大小，默认5
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        // 分页查询
        PageInfo<SysLog> pageInfo =
                sysLogService.findByPage(getLoginCompanyId(), pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("system/log/log-list");
        return mv;
    }
}