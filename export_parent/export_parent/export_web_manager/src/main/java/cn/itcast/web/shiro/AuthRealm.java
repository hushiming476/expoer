package cn.itcast.web.shiro;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Shiro提供的自定义realm类，实现查询数据库
 */
public class AuthRealm extends AuthorizingRealm{

    // 注入service
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    // 认证的方法，在这里需要查询数据库返回认证数据
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //1. 获取用户输入的邮箱信息(账号)
        String email = (String) token.getPrincipal();

        //2. 根据邮箱查询
        User user = userService.findByEmail(email);
        if (user == null){
            //shiro 会报错：账号错误。
            return null;
        }
        //3. 账号是正确的，所以获取数据库正确的密码
        String password = user.getPassword();

        //4. 返回
        // 参数1：登陆后的身份信息。subject.getPrincipal() 获取的就是这里的参数1.
        // 参数2：数据库正确的密码。我们只需要告诉shiro，数据库正确的密码即可。shiro会自动校验。
        // 参数3：realm的名称
        SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(user,password,this.getName());
        return sai;
    }

     // 授权的方法，在这里需要查询数据库返回权限数据
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //1. 获取用户信息
        //SecurityUtils.getSubject().getPrincipal();
        User user = (User) principals.getPrimaryPrincipal();

        //2. 根据用户的id查询用户的权限
        List<Module> moduleList = moduleService.findModulesByUserId(user.getId());

        //3. 返回对象，封装用户已经拥有的权限
        SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
        //4. 遍历用户的权限，添加到返回结果中
        if (moduleList != null && moduleList.size()>0){
            for (Module module : moduleList) {
                sai.addStringPermission(module.getName());
            }
        }
        return sai;
    }

}
