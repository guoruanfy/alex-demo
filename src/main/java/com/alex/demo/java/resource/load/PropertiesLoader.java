package com.alex.demo.java.resource.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by alexfang on 2015/7/1.
 */
public class PropertiesLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        try {
            Resource res = new ClassPathResource("app.properties");
            properties.load(res.getInputStream());
            System.setProperties(properties);

            System.out.println(System.getProperties().getProperty("author.name"));
        } catch (IOException e) {
            LOGGER.error("Failed to initialize thrift client!");
            throw new RuntimeException("Failed to initialize thrift client!");
        }
    }
}
