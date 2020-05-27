package com.peigong.offer.chapter2_javabasic.anonymousinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:58
 **/
public class Client {

    public static void main(String[] args) {
        Factory factory = new Factory();
        factory.workerWorking(new Worker() {
            @Override
            public void work() {
                System.out.println("anonymous worker working");
            }
        });
    }

}
