package com.peigong.offer.chapter4_data_structure.stack;

/**
 * @author: lilei
 * @create: 2020-05-30 11:32
 **/
public class Client {

    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        for (int i = 0; i < 3; i++) {
            System.out.println(stack.pop());
        }
        stack.push("1");
        stack.push("2");
        stack.push("3");
        for (int i = 0; i < 3; i++) {
            System.out.println(stack.pop());
        }
    }

}
