package com.peigong.offer.chapter3_concurrent.thread.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: lilei
 * @create: 2020-05-28 11:58
 **/
public class Client {

    public static void main(String[] args) {
        Counter counter = new Counter();
        counter.start();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        Thread.sleep(121);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    counter.add(new ExecuteResult(Thread.currentThread().getId() + "",Thread.currentThread().getName()));
                }
            });
        }
    }

}
