package com.peigong.offer.chapter2_javabasic.annotation;

import java.lang.reflect.Field;

/**
 * @author: lilei
 * @create: 2020-05-27 15:11
 **/
public class FruitInfoUtil {

    public static void getFruitInfo(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FruitProvider.class)) {
                FruitProvider fruitProvider = field.getAnnotation(FruitProvider.class);
                System.out.println("供应商编号:" + fruitProvider.id() + ",供应商名称：" + fruitProvider.name() + ",供应商地址：" + fruitProvider.address());
            }
        }
    }

    public static void main(String[] args) {
        getFruitInfo(Apple.class);
    }

}
