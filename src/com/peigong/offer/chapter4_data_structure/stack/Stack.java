package com.peigong.offer.chapter4_data_structure.stack;

/**
 * 基于数组实现的顺序栈
 * @author: lilei
 * @create: 2020-05-30 11:27
 **/
public class Stack<E> {

    private Object[] data = null;
    private int maxSize;
    private int top = -1;

    public Stack(){
        this(10);
    }

    public Stack(int initSize) {
        if (initSize >= 0) {
            maxSize = initSize;
            data = new Object[initSize];
        }else{
            throw new IllegalArgumentException("初始大小不能小于0");
        }
    }

    public boolean push(E e) {
        if (top == maxSize - 1) {
            throw new RuntimeException("栈已满");
        }else{
            data[++top] = e;
            return true;
        }
    }

    public E pop(){
        if (top == -1) {
            throw new RuntimeException("栈为空");
        }else{
            E e = (E) data[top--];
            data[top+1] = null;
            return e;
        }
    }

    public E peek(){
        if (top == -1) {
            throw new RuntimeException("栈为空");
        }else{
            return (E) data[top];
        }
    }
}
