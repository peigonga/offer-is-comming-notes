package com.peigong.offer.chapter4_data_structure.twowaylinkedlist;

/**
 * @author: lilei
 * @create: 2020-05-30 16:47
 **/
public class Client {

    public static void main(String[] args) {
        TwoWayLinkedList<Integer> list = new TwoWayLinkedList<>();
        list.addHead(1);
        list.addLast(2);
        list.addLast(3);
        list.deleteLast();
        System.out.println(1);
    }

}
