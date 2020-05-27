package com.peigong.offer.chapter2_javabasic.innerclass.memberinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:25
 **/
public class OutClass {

    private static int a;
    private int b;

    public class MemberInnerClass{

        public final static String FINAL_STATIC = "final static field";

        public void print(){
            System.out.println("outer a:" + a);
            System.out.println("outer b:" + b);
        }

    }

}
