package com.alex.demo.java.json;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

import java.io.InputStream;

public class JSONUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        SimpleFilterProvider filters = new SimpleFilterProvider();
        //thrift bean 不序列化set开头
        objectMapper.getSerializationConfig().addMixInAnnotations(TBase.class, ThriftBeanFilter.class); 
        filters.addFilter("ThriftBeanFilter", new ThriftBeanFilter());
        objectMapper.setFilters(filters);
    }
    
    public static <T> T readValue(String content, Class<T> valueType) {
        try {
            if (StringUtils.isNotEmpty(content)) {
                return objectMapper.readValue(content, valueType);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T readValue(InputStream ins, Class<T> valueType) {
        try {
            if (ins != null) {
                return objectMapper.readValue(ins, valueType);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String writeValueAsString(Object value) {
        try {
            if (value != null) {
                return objectMapper.writeValueAsString(value);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] writeValueAsBytes(Object value) {
        try {
            if (value != null) {
                return objectMapper.writeValueAsBytes(value);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        Student student = new Student();
        student.setAge(11);
        student.setName("aa");
        String studentJsonStr = JSONUtil.writeValueAsString(student);
        System.out.println(JSONUtil.writeValueAsString(student));

        Student student1 = JSONUtil.readValue(studentJsonStr, Student.class);
        System.out.println(student1.getAge());
    }


    private static class Student {

        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}

@JsonFilter("ThriftBeanFilter") 
class ThriftBeanFilter implements BeanPropertyFilter {
    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov, BeanPropertyWriter writer) throws Exception {
        if (!writer.getName().startsWith("set")) {
            writer.serializeAsField(bean, jgen, prov);
        }
    }
}