package com.peigong.offer.chapter3_concurrent.lock.sync;

/**
 * @author: lilei
 * @create: 2020-05-28 16:02
 **/
public class SynchronizedDemo {

    public static void main(String[] args) {
        SynchronizedDemo s = new SynchronizedDemo();
        //同步方法，所以哪个线程先获得锁，就是哪个线程直接运行完，另一个线程再运行
        new Thread(s::method1).start();
        new Thread(s::method2).start();
    }

    public synchronized void method1(){
        for (int i = 1; i < 3; i++) {
            System.out.println("method1 execute " + i + " time");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void method2(){
        for (int i = 1; i < 3; i++) {
            System.out.println("method2 execute " + i + " time");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
