package cn.itcast.cn.itcast.activemq3_spring;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 监听队列消息
 */
@Component
public class EmailListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            // 转换
            MapMessage msg = (MapMessage) message;
            // 获取消息内容
            String email = msg.getString("email");
            // 处理消息
            System.out.println("发送邮件：" + email);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
