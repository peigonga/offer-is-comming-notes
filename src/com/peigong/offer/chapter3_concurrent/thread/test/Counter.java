package com.peigong.offer.chapter3_concurrent.thread.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lilei
 * @create: 2020-05-28 11:49
 **/
public class Counter implements Runnable {

    private List<ExecuteResult> list;
    private AtomicInteger counter;

    private Thread thread;
    private boolean running;


    public Counter() {
        list = Collections.synchronizedList(new ArrayList<>());
        counter = new AtomicInteger();
    }

    public void start(){
        thread = new Thread(this, "Counter-Thread");
        running = true;
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                System.out.println("*******************");
                System.out.println("Counter Running...");
                System.out.println("*******************");
                Thread.sleep(344);
                if (list.size() > 0) {
                    List<ExecuteResult> l;

                    synchronized (list) {
                        l = new ArrayList<>(list);
                        list.clear();
                    }
                    for (ExecuteResult r : l) {
                        System.out.println(r.toString());
                        counter.incrementAndGet();
                    }
                }
                System.out.println("total execute:" + counter.get());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public void add(ExecuteResult result) {
        list.add(result);
    }
}
