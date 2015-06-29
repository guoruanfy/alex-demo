package com.alex.demo.java.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
* 异步发送短信
* */

public class SmsUtil<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);

    public static final int THREAD_COUNT = 10;


    private ConcurrentLinkedQueue<T> tasks;
    private ExecutorService executor;

    public SmsUtil() {
        this.tasks = new ConcurrentLinkedQueue<T>();
        this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
        startSendSmsTasks();
    }

    private void startSendSmsTasks() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            this.executor.execute(new SendSmsTask());
        }
    }

    private class SendSmsTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    T task = tasks.poll();
                    if (task == null) {
                        Thread.sleep(1000);
                    } else {
                        doTask(task);
                        LOGGER.debug("sending sms: {}", task);
                    }
                } catch (Exception e) {
                    LOGGER.error("{}", e);
                }
            }
        }
    }

    private void doTask(T task) {

    }

    public void send(T task) {
        tasks.offer(task);
    }

}
