package com.alex.demo.java.freemarker;

import com.alex.demo.java.json.JSONUtil;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerHelper {
    private static Configuration conf;

    static {
        conf = new Configuration();
        conf.setClassForTemplateLoading(FreeMarkerHelper.class, "/templates/");
        conf.setObjectWrapper(new DefaultObjectWrapper());
    }


    /**
     * @param root
     * @param template
     * @return
     * @throws IOException
     * @throws freemarker.template.TemplateException
     */
    public static String generate(Object root, String template) throws IOException, TemplateException {
        Writer outWriter = null;
        try {
            // 设置输出的字符类型
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            outWriter = new OutputStreamWriter(outStream, "utf-8");
            Template t = conf.getTemplate(template, "utf-8");
            Environment e = t.createProcessingEnvironment(root, outWriter);
            e.setOutputEncoding("utf-8");
            e.process();
            outWriter.flush();
            return outStream.toString();
        } finally {
            if (outWriter != null) {
                outWriter.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("packageName", "com.xiaomi.mipay");
        params.put("sendDate", "20150102");
        params.put("developerName", "alex");
        System.out.println(FreeMarkerHelper.generate(params, "LimtedAlert.ftl"));
    }
}
