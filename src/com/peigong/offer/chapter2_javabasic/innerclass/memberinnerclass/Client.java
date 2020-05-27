package com.peigong.offer.chapter2_javabasic.innerclass.memberinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:28
 **/
public class Client {

    public static void main(String[] args) {
        System.out.println(OutClass.MemberInnerClass.FINAL_STATIC);
        OutClass outClass = new OutClass();
        OutClass.MemberInnerClass memberInnerClass = outClass.new MemberInnerClass();
        memberInnerClass.print();
    }

}
