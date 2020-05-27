package com.peigong.offer.chapter2_javabasic.annotation;

import jdk.nashorn.internal.ir.annotations.Reference;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitProvider {

    public int id() default -1;

    public String name() default "";

    public String address() default "";

}
