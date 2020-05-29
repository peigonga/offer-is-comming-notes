package com.peigong.offer.chapter3_concurrent.queue.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * @author: lilei
 * @create: 2020-05-29 15:02
 **/
public class CountDownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            try {
                System.out.println("子线程1执行");
                Thread.sleep(3000);
                System.out.println("子线程1执行完成");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                System.out.println("子线程2执行");
                Thread.sleep(3000);
                System.out.println("子线程2执行完成");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            System.out.println("等待两个子线程执行完成");
            latch.await();
            System.out.println("全部子线程执行完成，主线程继续");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
