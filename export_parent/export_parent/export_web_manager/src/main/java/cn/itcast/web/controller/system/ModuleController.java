package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Module;
import cn.itcast.service.system.ModuleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController{

    @Autowired
    private ModuleService moduleService;

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
        // 分页查询
        PageInfo<Module> pageInfo =
                moduleService.findByPage(pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/module/module-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        // 查询全部模块，页面下拉列表使用
        List<Module> moduleList = moduleService.findAll();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/module/module-add");
        mv.addObject("moduleList",moduleList);
        return mv;
    }

    /**
     * 3. 添加或者修改
     */
    @RequestMapping("/edit")
    public String edit(Module module){
        if (StringUtils.isEmpty(module.getId())){
            // 添加
            moduleService.save(module);
        }
        else {
            // 修改
            moduleService.update(module);
        }
        return "redirect:/system/module/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        // 查询全部模块，页面下拉列表使用
        List<Module> moduleList = moduleService.findAll();

        // 根据id查询
        Module module = moduleService.findById(id);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("module",module);
        mv.addObject("moduleList",moduleList);
        mv.setViewName("system/module/module-update");
        return mv;
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        moduleService.delete(id);
        return "redirect:/system/module/list.do";
    }

}
