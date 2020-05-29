package com.peigong.offer.chapter3_concurrent.thread.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author: lilei
 * @create: 2020-05-28 11:18
 **/
public class CustomerCallable implements Callable<Result> {

    public static final String s = "dqnwoiejdqonmOIANSDOhjnwioqundOINASIODNOIA";

    @Override
    public Result call() throws Exception {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(s.charAt(random.nextInt(s.length())));
        }
        Result result = new Result();
        result.setRet(sb.toString());
        result.setStatus(random.nextInt(555));
        System.out.println(Thread.currentThread().getId() + " execute finish");
        return result;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Future<Result>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Callable<Result> c = new CustomerCallable();
            Future<Result> f = pool.submit(c);
            list.add(f);
        }
        System.out.println("*****");
        pool.shutdown();
        for (Future<Result> r : list) {
            System.out.println(r.get().toString());
        }
    }
}
