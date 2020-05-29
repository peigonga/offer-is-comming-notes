package com.peigong.offer.chapter3_concurrent.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: lilei
 * @create: 2020-05-28 16:25
 **/
public class InterruptiblyLock {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        InterruptiblyLock lock = new InterruptiblyLock();
        Thread thread1 = lock.lock1();
        Thread thread2 = lock.lock2();
        while (true) {
            if (System.currentTimeMillis() - start > 3000) {
                thread2.interrupt();
            }
        }
    }

    public ReentrantLock lock1 = new ReentrantLock();
    public ReentrantLock lock2 = new ReentrantLock();

    public Thread lock1(){
        Thread t = new Thread(() ->{
            try{
                lock1.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock2.lockInterruptibly();
                System.out.println(Thread.currentThread().getName() + " 执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (lock1.isHeldByCurrentThread()) {
                    lock1.unlock();
                }
                if (lock2.isHeldByCurrentThread()) {
                    lock2.unlock();
                }
                System.out.println(Thread.currentThread().getName() + " 退出");
            }
        });
        t.start();
        return t;
    }
    public Thread lock2(){
        Thread t = new Thread(() ->{
            try{
                lock2.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock1.lockInterruptibly();
                System.out.println(Thread.currentThread().getName() + " 执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (lock1.isHeldByCurrentThread()) {
                    lock1.unlock();
                }
                if (lock2.isHeldByCurrentThread()) {
                    lock2.unlock();
                }
                System.out.println(Thread.currentThread().getName() + " 退出");
            }
        });
        t.start();
        return t;
    }

}
