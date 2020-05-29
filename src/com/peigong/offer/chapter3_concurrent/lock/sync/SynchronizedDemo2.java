package com.peigong.offer.chapter3_concurrent.lock.sync;

/**
 * @author: lilei
 * @create: 2020-05-28 16:02
 **/
public class SynchronizedDemo2 {

    public static void main(String[] args) {
        SynchronizedDemo2 s = new SynchronizedDemo2();
        SynchronizedDemo2 s2 = new SynchronizedDemo2();
        //非静态方法，锁住的是对象
        new Thread(s::method1).start();
        new Thread(s2::method2).start();
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
