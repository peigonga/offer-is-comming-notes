package com.peigong.offer.chapter3_concurrent.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: lilei
 * @create: 2020-05-28 16:18
 **/
public class ReentrantLockDemo implements Runnable{

    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 10; j++) {
            lock.lock();
            //lock.lock();//可重入锁
            try {
                i++;
            }finally {
                lock.unlock();
                //lock.unlock();//可重入锁
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockDemo demo = new ReentrantLockDemo();
        Thread t1 = new Thread(demo);
        t1.start();
        t1.join();//t1线程执行完
        System.out.println(i);
    }
}
