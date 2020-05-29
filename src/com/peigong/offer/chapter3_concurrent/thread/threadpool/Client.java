package com.peigong.offer.chapter3_concurrent.thread.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: lilei
 * @create: 2020-05-28 11:37
 **/
public class Client {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            });
            System.out.println("add " + i);
            if (i > 100) {
                pool.shutdown();
            }
        }
        System.out.println("shutdown");
        pool.shutdown();
    }

}
