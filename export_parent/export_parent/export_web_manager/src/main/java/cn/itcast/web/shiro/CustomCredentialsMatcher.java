package cn.itcast.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 自定义的凭证匹配器，对用户输入的密码按照自己指定的方式进行加密。
 * 输入：
 *      用户名： company
 *      密码：   1
 *      加密：   md5加密 + 把用户名作为盐
 *              new Md5Hash(1,company);
 */
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

    /**
     * 对用户输入的密码进行加密
     * @param token 用户输入的信息：账号、密码
     * @param info  认证信息，存储了数据库中的正确密码
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        //1. 获取输入的用户名
        String email = (String) token.getPrincipal();

        //2. 获取用户输入的密码
        String password = new String((char[])token.getCredentials());

        //3. 对用户输入的密码加密，加盐
        String encodePassword = new Md5Hash(password,email).toString();
        
        //4. 获取数据中正确的密码（realm中的认证方法返回对象的构造函数的第二个参数）
        String dbPassword = (String) info.getCredentials();

        //5. 判断： 用户输入的密码加密加盐后与数据库正确的密码对比
        return encodePassword.equals(dbPassword);
    }
}














