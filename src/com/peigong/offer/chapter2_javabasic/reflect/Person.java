package com.peigong.offer.chapter2_javabasic.reflect;

/**
 * @author: lilei
 * @create: 2020-05-27 14:49
 **/
public class Person {

    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void talk(String cnt){
        System.out.println(name + " said:" + cnt);
    }

}
