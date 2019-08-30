package cn.itcast.cn.itcast.activemq2_many;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 队列消息，消息的消费方
 */
public class Queue2Consumer {
    public static void main(String[] args) throws JMSException {
        //1. 创建连接的工厂
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("admin","admin","tcp://192.168.12.132:61616");

        //2. 创建连接
        Connection connection = factory.createConnection();

        //3. 启用连接
        connection.start();

        //3. 创建session对象
        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4. 创建消息的目的地
        Queue queue = session.createQueue("email");

        //5. 创建消息的消费者
        MessageConsumer consumer = session.createConsumer(queue);

        //6. 设置监听器，监听容器中的消息,消费消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                // 发消息的时候是map消息，所以这里转换为map消息
                MapMessage msg = (MapMessage) message;
                try {
                    // 获取消息内容
                    String email = msg.getString("email");
                    System.out.println("消息消费，发送邮件：" + email);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
