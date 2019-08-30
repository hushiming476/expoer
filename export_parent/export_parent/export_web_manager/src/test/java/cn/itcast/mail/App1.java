package cn.itcast.mail;

import cn.itcast.web.utils.MailUtil;

public class App1 {
    public static void main(String[] args) throws Exception {
        MailUtil.sendMsg("itcast004@sina.com","第一封邮件！","通过java发送邮件，很骚！");
    }
}
