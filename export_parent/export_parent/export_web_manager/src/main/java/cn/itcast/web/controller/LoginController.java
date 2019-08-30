package cn.itcast.web.controller;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    /**
     * 登陆成功后要进入主页
     * 1. 访问主页
     *    http://localhost:8080/index.jsp --->location.href = "login.do";
     * 2. 直接进入
     *    /WEB-INF/pages/home/main.jsp
     * 3. 结果：
     *    页面404，因为在main.jsp的iframe的src中，找地址：home.do

    @RequestMapping("/login")
    public String login(String email,String password){
        // 判断
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            return "forward:/login.jsp";
        }

        // 根据邮箱查询
        User user = userService.findByEmail(email);
        // 判断
        if (user != null){
            // 判断密码
            if (password.equals(user.getPassword())){
                // 登陆成功
                session.setAttribute("loginUser",user);
                // 根据用户id查询用户的权限（根据用户的degree级别判断）
                List<Module> moduleList = moduleService.findModulesByUserId(user.getId());
                session.setAttribute("moduleList",moduleList);
                return "home/main";
            } else {
                // 密码错误
                request.setAttribute("error","密码错误!");
                return "forward:/login.jsp";
            }
        } else {
            // 账号错误
            request.setAttribute("error","账号错误!");
            return "forward:/login.jsp";
        }
    }
     */


    @RequestMapping("/login")
    public String login(String email,String password){
        // 判断
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            return "forward:/login.jsp";
        }

        //subject---->securityManager---->realm

        try {
            // 创建subject对象
            Subject subject = SecurityUtils.getSubject();
            // 创建token，封装用户输入的账号密码
            UsernamePasswordToken token = new UsernamePasswordToken(email,password);
            // 登陆认证.  会去到realm中的认证方法。
            subject.login(token);

            // 获取认证的信息(用户信息)
            User user = (User) subject.getPrincipal();
            // 登陆成功
            session.setAttribute("loginUser",user);
            // 根据用户id查询用户的权限（根据用户的degree级别判断）
            List<Module> moduleList = moduleService.findModulesByUserId(user.getId());
            session.setAttribute("moduleList",moduleList);
            return "home/main";
        } catch (Exception e) {
            e.printStackTrace();
            // 账号错误
            request.setAttribute("error","账号或密码错误!");
            return "forward:/login.jsp";
        }
    }



    /**
     * 4. 转发到/WEB-INF/pages/home/home.jsp
     */
    @RequestMapping("/home")
    public String home(){
        return "home/home";
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public String logout(){
        // 清空shiro中的认证信息
        SecurityUtils.getSubject().logout();

        // 清空session中登陆用户
        session.removeAttribute("loginUser");
        // 销毁session
        session.invalidate();
        return "forward:/login.jsp";
    }


}
