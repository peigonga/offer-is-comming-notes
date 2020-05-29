package com.peigong.offer.chapter3_concurrent.lock.sync;

/**
 * @author: lilei
 * @create: 2020-05-28 16:02
 **/
public class StaticMethodSynchronized {

    public static void main(String[] args) {
        //静态方法，锁住的是类
        new Thread(StaticMethodSynchronized::method1).start();
        new Thread(StaticMethodSynchronized::method2).start();
    }

    public static synchronized void method1(){
        for (int i = 1; i < 3; i++) {
            System.out.println("method1 execute " + i + " time");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void method2(){
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
