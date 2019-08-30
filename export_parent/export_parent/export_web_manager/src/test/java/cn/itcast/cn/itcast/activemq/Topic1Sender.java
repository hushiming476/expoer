package cn.itcast.cn.itcast.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 主题消息，消息的发送方
 */
public class Topic1Sender {
    public static void main(String[] args) throws JMSException {
        //1. 创建连接的工厂
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("admin","admin","tcp://192.168.12.132:61616");

        //2. 创建连接
        Connection connection = factory.createConnection();

        //3. 启用连接
        connection.start();

        //3. 创建session对象
        //参数1：是否需要启用事务。如果设置为true，就需要session.commit();
        //参数2：自动应答机制(服务的消费者从消息容器中消费消息后自动通知容器)
        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4. 创建主题消息目的地
        Topic topic = session.createTopic("sms");

        //5. 创建消息对象，存储邮箱
        MapMessage mapMessage = session.createMapMessage();
        mapMessage.setString("sms","18665591009");

        //6. 创建消息的生产者
        MessageProducer producer = session.createProducer(topic);

        //7. 发送消息
        producer.send(mapMessage);

        //8. 释放资源
        session.close();
        connection.close();
    }
}
