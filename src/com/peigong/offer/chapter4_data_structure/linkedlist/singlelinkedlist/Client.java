package com.peigong.offer.chapter4_data_structure.linkedlist.singlelinkedlist;

/**
 * @author: lilei
 * @create: 2020-05-30 16:10
 **/
public class Client {

    public static void main(String[] args) {
        SingleLinkedList list = new SingleLinkedList();
        list.addHead(1);
        list.addHead(2);
        list.addHead(3);
        list.delete(2);
        System.out.println(list);
    }

}
