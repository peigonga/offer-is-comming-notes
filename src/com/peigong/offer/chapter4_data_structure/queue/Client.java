package com.peigong.offer.chapter4_data_structure.queue;

/**
 * @author: lilei
 * @create: 2020-05-30 13:40
 **/
public class Client {

    public static void main(String[] args) {
        Queue<String> queue = new Queue<>();
        queue.add("1");
        queue.add("2");
        queue.add("3");
        for (int i = 0; i < 3; i++) {
            String poll = queue.poll();
            System.out.println(poll);
        }
    }

}
