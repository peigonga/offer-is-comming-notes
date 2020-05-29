package com.peigong.offer.chapter3_concurrent.queue.semaphore;

import java.util.concurrent.Semaphore;

/**
 * @author: lilei
 * @create: 2020-05-29 15:39
 **/
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < 5; i++) {
            new Thread(new Worker("worker"+i,semaphore)).start();
        }
    }

    static class Worker implements Runnable {

        private Semaphore semaphore;
        private String name;

        public Worker(String name,Semaphore semaphore) {
            this.name = name;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println(name + " using printer");
                Thread.sleep(2000);
                System.out.println(name + " done");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
