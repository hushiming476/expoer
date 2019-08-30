package cn.itcast.listener;

import cn.itcast.utils.MailUtil;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class EmailListener implements MessageListener{
    @Override
    public void onMessage(Message message) {
        try {
            MapMessage mapMessage = (MapMessage) message;
            String email = mapMessage.getString("email");
            String subject = mapMessage.getString("subject");
            String content = mapMessage.getString("content");
            // 调用工具类发送邮件
            MailUtil.sendMsg(email, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
