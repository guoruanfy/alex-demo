package com.alex.demo.java.spring.initialize;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by alexfang on 2015/7/1.
 */
@Service
public class School implements InitializingBean {
    private String schoolName = null;

    public String getSchoolName() {
        return schoolName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == schoolName) {
            schoolName = "WuhanUniversity";
        }
    }
}
