package com.peigong.offer.chapter2_javabasic.number;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: lilei
 * @create: 2020-06-20 16:42
 **/
public class ClientTest {

    {
        System.out.println("1");
    }

    public ClientTest(){
        System.out.println(2);
    }

    static{
        System.out.println(3);
    }

    public static void main(String[] args) {
        ClientTest test = new ClientTest();
    }

}
