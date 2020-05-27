package com.peigong.offer.chapter2_javabasic.general.generalclass;

/**
 * @author: lilei
 * @create: 2020-05-27 16:11
 **/
public class GeneralClass<T> {

    public static void main(String[] args) {
        GeneralClass<String> g = new GeneralClass<>();
        g.setT("1");
    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
