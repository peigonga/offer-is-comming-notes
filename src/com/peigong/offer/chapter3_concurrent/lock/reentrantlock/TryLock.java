package com.peigong.offer.chapter3_concurrent.lock.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: lilei
 * @create: 2020-05-28 16:25
 **/
public class TryLock {

    public static void main(String[] args) {
        TryLock lock = new TryLock();
        lock.lock1();
        lock.lock2();
    }

    public ReentrantLock lock1 = new ReentrantLock();
    public ReentrantLock lock2 = new ReentrantLock();

    public Thread lock1(){
        Thread t = new Thread(() ->{
            lock1.lock();
            try {
                Thread.sleep(500);
                if (lock2.tryLock(5, TimeUnit.SECONDS)) {
                    System.out.println(Thread.currentThread().getName() + " 执行完毕");
                    lock2.unlock();
                }else{
                    System.out.println(Thread.currentThread().getName() + " 未获得2号锁");
                }
                lock1.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        t.start();
        return t;
    }
    public Thread lock2(){
        Thread t = new Thread(() ->{
            lock2.lock();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock1.lock();
            System.out.println(Thread.currentThread().getName() + " 执行完毕");
            lock1.unlock();
            lock2.unlock();
        });
        t.start();
        return t;
    }

}
