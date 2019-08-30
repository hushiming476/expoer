package cn.itcast.cn.itcast.activemq3_spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-activemq-send.xml")
public class App1_sender {

    // 注入发送消息的工具类
    @Autowired
    private JmsTemplate jmsQueueTemplate;
    @Autowired
    private JmsTemplate jmsTopicTemplate;

    // 发送队列消息。
    @Test
    public void sendQueue(){
        jmsQueueTemplate.send("email", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // 创建消息
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("email","610731230@qq.com");
                return mapMessage;
            }
        });
    }


    // 发送主题消息。
    @Test
    public void sendTopic(){
        jmsTopicTemplate.send("sms", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // 创建消息
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone","18665591009");
                return mapMessage;
            }
        });
    }
}
