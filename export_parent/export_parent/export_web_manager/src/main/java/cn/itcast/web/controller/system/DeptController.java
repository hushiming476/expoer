package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
@RequestMapping("/system/dept")
public class DeptController extends BaseController{

    @Autowired
    private DeptService deptService;

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
        // 企业id：后期从当前登陆用户中获取用户的企业信息。现在先写死。
        String companyId = getLoginCompanyId();
        // 分页查询
        PageInfo<Dept> pageInfo =
                deptService.findByPage(companyId, pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/dept/dept-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/system/dept/toAdd.do
     * 保存数据：全部部门，页面下拉列表回显
     * 响应地址：/WEB-INF/pages/system/dept/dept-add.jsp
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        String companyId = getLoginCompanyId();
        // 查询全部部门
        List<Dept> deptList = deptService.findAll(companyId);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/dept/dept-add");
        mv.addObject("deptList",deptList);
        return mv;
    }

    /**
     * 3. 添加或者修改
     */
    @RequestMapping("/edit")
    public String edit(Dept dept){
        // 获取登陆用户所属企业信息
        dept.setCompanyId(getLoginCompanyId());
        dept.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(dept.getId())){
            // 添加
            deptService.save(dept);
        }
        else {
            // 修改
            deptService.update(dept);
        }
        return "redirect:/system/dept/list.do";
    }

    /**
     * 4. 进入修改页面
     * 请求地址：http://localhost:8080/system/dept/toUpdate.do?id=100
     * 保存数据：部门对象
     * 响应地址：/WEB-INF/pages/system/dept/dept-update.jsp
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        String companyId = getLoginCompanyId();

        // 根据id查询
        Dept dept = deptService.findById(id);

        // 查询部门
        List<Dept> deptList = deptService.findAll(companyId);

        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("dept",dept);
        mv.addObject("deptList",deptList);
        mv.setViewName("system/dept/dept-update");
        return mv;
    }

    /**
     * 5. 删除
     * ajax请求地址：/system/dept/delete.do
     * 参数：       id
     * 返回json：   {message:""}
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,String> delete(String id){
        //1. 调用service删除
        boolean flag = deptService.delete(id);
        // 返回的对象，会自动转换为json
        Map<String,String> result = new HashMap<>();
        //2. 判断
        if (flag){
            // 删除成功
            result.put("message","删除成功！");
        }else{
            result.put("message","删除失败，删除的部门下有子部门，不能删除！");
        }
        return result;
    }

}
