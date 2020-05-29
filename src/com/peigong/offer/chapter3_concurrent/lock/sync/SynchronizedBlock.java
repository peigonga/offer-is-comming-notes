package com.peigong.offer.chapter3_concurrent.lock.sync;

/**
 * @author: lilei
 * @create: 2020-05-28 16:13
 **/
public class SynchronizedBlock {

    public static void main(String[] args) {
        SynchronizedBlock b1 = new SynchronizedBlock();
        new Thread(b1::method1).start();
        new Thread(b1::method2).start();
    }

    private final String field0 = "";

    public void method1(){
        synchronized (field0) {
            try {
                for (int i = 0; i < 2; i++) {
                    System.out.println("method1 execute:" + i);
                    Thread.sleep(2000);
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void method2(){
        synchronized (field0) {
            try {
                for (int i = 0; i < 2; i++) {
                    System.out.println("method2 execute:" + i);
                    Thread.sleep(2000);
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
