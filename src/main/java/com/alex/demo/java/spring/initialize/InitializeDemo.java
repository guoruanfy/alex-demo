package com.alex.demo.java.spring.initialize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by alexfang on 2015/7/1.
 */
@Service
public class InitializeDemo {
    @Autowired
    School school;

    public static void main(String[] args) {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InitializeDemo initializeDemo = context.getBean(InitializeDemo.class);
        initializeDemo.displaySchoolName();
    }

    private void displaySchoolName() {
        System.out.println(school.getSchoolName());
    }
}
