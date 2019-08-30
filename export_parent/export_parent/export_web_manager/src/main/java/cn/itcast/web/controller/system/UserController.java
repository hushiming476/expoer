package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Role;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.RoleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.MailUtil;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;

    /**
     * 1. 分页查询
     * @param pageNum  当前页，默认1
     * @param pageSize 页大小，默认5
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions("用户管理")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize){

        // 获取subject对象
        //Subject subject = SecurityUtils.getSubject();
        // 权限校验（访问当前用户列表，需要权限：“用户管理”）
        //subject.checkPermission("用户管理");


        // 企业id：后期从当前登陆用户中获取用户的企业信息。现在先写死。
        String companyId = getLoginCompanyId();
        // 分页查询
        PageInfo<User> pageInfo =
                userService.findByPage(companyId, pageNum, pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/user/user-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        String companyId = getLoginCompanyId();
        // 查询全部部门
        List<Dept> deptList = deptService.findAll(companyId);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/user/user-add");
        mv.addObject("deptList",deptList);
        return mv;
    }

    /**
     * 3. 添加或者修改
     */
    // 注入jmsTemplate，发送消息到ActiveMQ消息容器的工具类
    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("/edit")
    public String edit(User user){
        // 获取登陆用户所属企业信息
        user.setCompanyId(getLoginCompanyId());
        user.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(user.getId())){
            // 添加
            userService.save(user);
            // 添加用户成功，发送邮件
            if (user.getEmail() != null && !"".equals(user.getEmail())) {
                String to = user.getEmail();
                // 处理发送邮件的业务
                String subject = "新员工入职通知";
                String content = "欢迎你来到SaasExport大家庭，我们是一个充满激情的团队，不是996哦！";
                try {

                    // 调用工具类发送邮件
                    //MailUtil.sendMsg(to, subject, content);

                    // 发送消息到消息容器，再由消息处理系统实现发送邮件。
                    jmsTemplate.send("email", new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            MapMessage mapMessage = session.createMapMessage();
                            // 设置消息内容
                            mapMessage.setString("email",to);
                            mapMessage.setString("subject",subject);
                            mapMessage.setString("content",content);
                            return mapMessage;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            // 修改
            userService.update(user);
        }
        return "redirect:/system/user/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        String companyId = getLoginCompanyId();

        // 根据id查询
        User user = userService.findById(id);

        // 查询部门
        List<Dept> deptList = deptService.findAll(companyId);

        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("user",user);
        mv.addObject("deptList",deptList);
        mv.setViewName("system/user/user-update");
        return mv;
    }

    /**
     * 5. 删除
     * ajax请求地址：/system/user/delete.do
     * 参数：       id
     * 返回json：   {message:""}
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,String> delete(String id){
        //1. 调用service删除
        boolean flag = userService.delete(id);
        // 返回的对象，会自动转换为json
        Map<String,String> result = new HashMap<>();
        //2. 判断
        if (flag){
            // 删除成功
            result.put("message","删除成功！");
        }else{
            result.put("message","删除失败，删除的用户有被引用，不能删除！");
        }
        return result;
    }

    /**
     * 6. 用户分配角色（1）进入用户角色页面
     * 功能入口：用户管理列表点击角色按钮
     * 提交地址：http://localhost:8080/system/user/roleList.do
     * 提交参数：id=1
     * 响应地址：/WEB-INF/pages/system/user/user-role.jsp
     */
    @RequestMapping("/roleList")
    public String roleList(String id){
        //1. 根据用户id查询用户 （因为页面要显示用户名）
        User user = userService.findById(id);

        //2. 查询所有角色 （因为页面要通过复选框显示所有角色）  [船运专员，船运经理,报运经理...]
        List<Role> roleList = roleService.findAll(getLoginCompanyId());

        //3. 查询用户拥有的角色 （因为复选框要默认选中）
        List<Role> userRoleList = roleService.findRoleByUserId(id);

        //4. 定义一个字符串保存用户已经拥有的角色
        String roleStr = "";// [船运专员，船运经理]
        if (userRoleList != null && userRoleList.size()>0){
            for (Role role : userRoleList) {
                roleStr += role.getId() + ",";
            }
        }

        //5. 保存
        request.setAttribute("user",user);
        request.setAttribute("roleList",roleList);
        request.setAttribute("roleStr",roleStr);
        return "system/user/user-role";
    }

    /**
     * 6. 用户分配角色（2）保存用户角色
     */
    @RequestMapping("/changeRole")
    public String changeRole(String userId,String[] roleIds){
        // 给用户分配角色
        userService.changeRole(userId,roleIds);
        return "redirect:/system/user/list.do";
    }

}










