package com.peigong.offer.chapter4_data_structure.queue;

/**
 * @author: lilei
 * @create: 2020-05-30 11:40
 **/
public class Queue<E> {

    private Object[] data = null;
    private int maxSize;
    //队头
    private int front;
    //队尾
    private int rear;

    public Queue(){
        this(10);
    }

    public Queue(int initialSize) {
        if (initialSize >= 0) {
            this.maxSize = initialSize;
            data = new Object[maxSize];
        }else{
            throw new IllegalArgumentException("初始大小不能小于0");
        }
    }

    public boolean add(E e) {
        if (rear == maxSize) {
            throw new RuntimeException("队列已满");
        }else{
            data[rear++] = e;
            return true;
        }
    }

    public E poll(){
        if (empty()) {
            throw new RuntimeException("队列为空");
        }
        E value = (E)data[front];
        data[front++] = null;
        return value;
    }

    public E peek(){
        if (empty()) {
            throw new RuntimeException("队列为空");
        }
        return (E)data[front];
    }

    public boolean empty(){
        if (data.length == 0) {
            return true;
        }
        for (Object o : data) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }

}
