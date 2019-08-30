package cn.itcast.shiro;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;

public class App {
    // md5加密
    @Test
    public void md5(){
        Md5Hash md5Hash = new Md5Hash("1");
        //c4ca4238a0b923820dcc509a6f75849b
        System.out.println(md5Hash.toString());
    }

    /**
     * md5加密, 加盐
     * 好处： 更安全
     *
     * 彩虹表：
     * 明文       密文
     *  1        cd91834220d4c57b6c2d9702e41ae008
     *
     *  根据彩虹表进行撞库。可以获取加密前的密码，所以单独使用md5不安全。
     */
    @Test
    public void md5_salt(){
        Md5Hash md5Hash = new Md5Hash("1","lisi@export.com");
        System.out.println(md5Hash);
    }

    // md5加密, 加随机盐
    @Test
    public void md5_salt2(){
        // 生产随机盐
        SecureRandomNumberGenerator srn = new SecureRandomNumberGenerator();
        String salt = srn.nextBytes().toHex();
        System.out.println("随机盐:" + salt);

        Md5Hash md5Hash = new Md5Hash("1",salt);

        System.out.println("加密后：" + md5Hash.toString());
    }

    // md5加密, 加盐, 加迭代次数
    @Test
    public void md5_salt3(){
        Md5Hash md5Hash = new Md5Hash("1","company-admin@export.com",30000);
        //cd91834220d4c57b6c2d9702e41ae008
        System.out.println(md5Hash);
    }
}
