package cn.itcast.consumer;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class EmailConsumerTest {
    public static void main(String[] args) throws IOException {
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("applicationContext-activemq-consumer.xml");
        System.in.read();
    }
}