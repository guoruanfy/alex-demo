package com.alex.demo.java.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ThriftCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftCallback.class);
    private static final String suffix = "$Iface";
    //作为缓存使用
    private static Map<String, Object> clients = new HashMap<String, Object>();
    private static ReadWriteLock statusLock = new ReentrantReadWriteLock();

    public static <T> T doCallback(String classFullName, String methodName, Class<T> t) throws Exception {
        return doCallback(classFullName, methodName, null, t);
    }

    public static void doCallback(String classFullName, String methodName, List params) throws Exception {
        doCallback(classFullName, methodName, params, null);
    }

    public static void doCallback(String classFullName, String methodName) throws Exception {
        doCallback(classFullName, methodName, null, null);
    }

    /**
     * @param classFullName 类全名
     * @param methodName    调用的方法
     * @param params        调用的参数
     * @param t             返回值类型
     * @param <T>           泛型
     * @return 方法执行后的返回值
     * @throws Exception
     */
    public static <T> T doCallback(String classFullName, String methodName, List params, Class<T> t) throws Exception {
        Object client = getClient(classFullName);
        Object invoke;
        if (params != null && params.size() != 0) {
            Class[] types = new Class[params.size()];
            Method[] methods = client.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    types = method.getParameterTypes();
                    break;
                }
            }
            invoke = client.getClass().getMethod(methodName, types).invoke(client, params.toArray());
        } else {
            invoke = client.getClass().getMethod(methodName, null).invoke(client, t);
        }
        return (T) invoke;
    }

    //获取thrift服务
    private static Object getClient(String classFullName) throws ClassNotFoundException {
        Object client = null;
        statusLock.readLock().lock();
        try {
            client = clients.get(classFullName);
        } finally {
            statusLock.readLock().unlock();
        }

        if (client != null) {
            return client;
        } else {
            return createClient(classFullName);
        }
    }

    private static Object createClient(String classFullName) throws ClassNotFoundException {
        statusLock.writeLock().lock();
        try {
            Object client = clients.get(classFullName);
            if (client == null) {
//                TODO create client
                clients.put(classFullName, client);
            }
            return client;
        } finally {
            statusLock.writeLock().unlock();
        }
    }
}
