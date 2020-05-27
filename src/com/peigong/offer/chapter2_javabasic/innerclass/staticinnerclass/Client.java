package com.peigong.offer.chapter2_javabasic.innerclass.staticinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:27
 **/
public class Client {

    public static void main(String[] args) {
        System.out.println(OuterClass.StaticInnerClass.INNER_STATIC);
        OuterClass.StaticInnerClass c = new OuterClass.StaticInnerClass();
        c.getClassName();
    }
}
