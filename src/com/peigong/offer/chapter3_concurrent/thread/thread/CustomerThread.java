package com.peigong.offer.chapter3_concurrent.thread.thread;

/**
 * @author: lilei
 * @create: 2020-05-28 11:08
 **/
public class CustomerThread extends Thread {

    @Override
    public void run() {
        System.out.println("Do Something...");
    }

    public static void main(String[] args) {
        CustomerThread t = new CustomerThread();
        t.start();
    }
}
