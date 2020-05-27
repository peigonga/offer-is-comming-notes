package com.peigong.offer.chapter2_javabasic.general.generalmethod;

import java.util.Random;

/**
 * @author: lilei
 * @create: 2020-05-27 16:08
 **/
public class GeneralMethod {

    public static <T> T random(T[] ts) {
        Random random = new Random();
        int i = random.nextInt(ts.length);
        return ts[i];
    }

    public static void main(String[] args) {
        Integer random = random(new Integer[]{1, 2, 3});
        System.out.println(random);
    }

}
