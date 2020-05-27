package com.peigong.offer.chapter2_javabasic.innerclass.staticinnerclass;

/**
 * @author: lilei
 * @create: 2020-05-27 15:19
 **/
public class OuterClass {

    private static String className = "staticInnerClass";


    public static class StaticInnerClass {

        public static String INNER_STATIC = "innerClass";

        public void getClassName(){
            System.out.println("className:" + className);
        }

    }


}
