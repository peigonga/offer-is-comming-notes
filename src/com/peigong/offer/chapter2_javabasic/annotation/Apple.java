package com.peigong.offer.chapter2_javabasic.annotation;

/**
 * @author: lilei
 * @create: 2020-05-27 15:10
 **/
public class Apple {

    @FruitProvider(id=1,name = "大苹果经销商",address = "xxxxxx")
    private String appleProvider;

    public String getAppleProvider() {
        return appleProvider;
    }

    public void setAppleProvider(String appleProvider) {
        this.appleProvider = appleProvider;
    }
}
