package com.peigong.offer.chapter2_javabasic.innerclass.partinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:53
 **/
public class OutClass {

    private static int a;
    private int b;

    public void partClassTest(final int c){
        final int d = 1;
        class PartClass{
            public void print(){
                System.out.println(c);
                System.out.println(a);
            }
        }
        new PartClass().print();
    }

}
