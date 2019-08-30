package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.RoleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController{

    @Autowired
    private RoleService roleService;
    @Autowired
    private ModuleService moduleService;;

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
        PageInfo<Role> pageInfo =
                roleService.findByPage(companyId, pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/role/role-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "system/role/role-add";
    }

    /**
     * 3. 添加或者修改
     */
    @RequestMapping("/edit")
    public String edit(Role role){
        // 获取登陆用户所属企业信息
        role.setCompanyId(getLoginCompanyId());
        role.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(role.getId())){
            // 添加
            roleService.save(role);
        }
        else {
            // 修改
            roleService.update(role);
        }
        return "redirect:/system/role/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        // 根据id查询
        Role role = roleService.findById(id);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("role",role);
        mv.setViewName("system/role/role-update");
        return mv;
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        roleService.delete(id);
        return "redirect:/system/role/list.do";
    }

    /**
     * 6.角色权限（1）进入角色权限页面
     * 请求地址：http://localhost:8080/system/role/roleModule.do?roleid=1
     * 转发页面：/WEB-INF/pages/system/role/role-module.jsp
     */
    @RequestMapping("/roleModule")
    public String roleModule(String roleid){
        // 根据角色id查询
        Role role = roleService.findById(roleid);
        // 保存
        request.setAttribute("role",role);
        return "system/role/role-module";
    }

    /**
     * 7.角色权限（2）role-module.jsp发送异步请求，初始化权限树
     * 请求地址：/system/role/getZtreeNodes.do
     * 请求参数：{"roleId":1}
     * 返回数据格式：
     *    ztree = [
     *       { id:2, pId:0, name:"", open:true},
     *       { id:2, pId:0, name:"", open:true,checked:true},
     *    ]
     *  定义对象封装json格式数据： List<Map<String,Object>>
     */
    @RequestMapping("/getZtreeNodes")
    @ResponseBody
    public List<Map<String,Object>> getZtreeNodes(String roleId){
        //1. 定义方法返回的集合
        List<Map<String,Object>> result = new ArrayList<>();

        //2. 查询所有的权限
        List<Module> moduleList = moduleService.findAll();

        //3. 查询角色已经拥有的权限. 查询条件：角色ID
        List<Module> roleModuleList =  moduleService.findModulesByRoleId(roleId);

        //4. 构造返回的结果
        for(Module module : moduleList){
            //4.1 创建map集合，封装每一个权限对象的数据
            Map<String,Object> map = new HashMap<>();
            map.put("id",module.getId());
            map.put("pId",module.getParentId());
            map.put("name",module.getName());
            map.put("open",true);
            //4.2 判断是否需要选中
            if (roleModuleList != null && roleModuleList.size()>0){
                if (roleModuleList.contains(module)){
                    map.put("checked",true);
                }
            }

            //4.3 把map添加到list集合
            result.add(map);
        }

        return result;
    }
    /**
     * 7.角色权限（3）role-module.jsp点击保存，给角色添加权限
     */
    @RequestMapping("/updateRoleModule")
    public String updateRoleModule(String roleId,String moduleIds){
        // 调用service，给角色分配权限
        roleService.updateRoleModule(roleId,moduleIds);
        return "redirect:/system/role/list.do";
    }
}
