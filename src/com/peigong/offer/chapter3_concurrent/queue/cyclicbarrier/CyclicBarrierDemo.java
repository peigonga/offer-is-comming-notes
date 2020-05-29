package com.peigong.offer.chapter3_concurrent.queue.cyclicbarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author: lilei
 * @create: 2020-05-29 15:20
 **/
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int n = 4;
        CyclicBarrier barrier = new CyclicBarrier(n);
        for (int i = 0; i < n; i++) {
            new Thread(new Business(barrier)).start();
        }
    }

    static class Business implements Runnable{

        private CyclicBarrier barrier;

        public Business(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {

                Thread.sleep(new Random().nextInt(6000));
                System.out.println("线程执行前准备工作完成，等待");
                barrier.await();
                System.out.println("全部就绪，全部完成");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
