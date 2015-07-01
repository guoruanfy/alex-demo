package com.alex.demo.java.spring.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by alexfang on 2015/7/1.
 */
@Service
public class LogTest {

    @Log
    public int add(int b, int a) {
        return a + b;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        LogTest logTest = applicationContext.getBean(LogTest.class);
        System.out.println(logTest.add(1, 2));
    }
}
