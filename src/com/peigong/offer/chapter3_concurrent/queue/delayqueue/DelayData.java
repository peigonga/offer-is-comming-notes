package com.peigong.offer.chapter3_concurrent.queue.delayqueue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author: lilei
 * @create: 2020-05-29 14:18
 **/
public class DelayData implements Delayed {

    private int number;

    private long time = 1;

    public DelayData(int number, long time, TimeUnit unit) {
        this.number = number;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //System.out.println(unit);
        return this.time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayData dd = (DelayData) o;
        return Integer.compare(this.number,dd.number);
    }

    public static void main(String[] args) {
        DelayQueue<DelayData> queue = new DelayQueue<>();
        queue.add(new DelayData(9,5,TimeUnit.SECONDS));
        queue.add(new DelayData(6,5,TimeUnit.SECONDS));
        queue.add(new DelayData(3,5,TimeUnit.SECONDS));
        while (queue.size() > 0) {
            try {
                DelayData take = queue.take();
                System.out.println(take.number);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
