package com.peigong.offer.chapter2_javabasic.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author: lilei
 * @create: 2020-05-27 14:50
 **/
public class Client {

    public static void main(String[] args) throws Exception{
        Class clazz = Class.forName("com.peigong.offer.chapter2_javabasic.reflect.Person");
        Method method = clazz.getDeclaredMethod("talk", String.class);
        Constructor constructor = clazz.getConstructor(String.class);
        Object o = constructor.newInstance("Tim");
        method.invoke(o, "reflect");
    }

}
