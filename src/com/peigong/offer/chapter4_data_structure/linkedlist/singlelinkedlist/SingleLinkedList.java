package com.peigong.offer.chapter4_data_structure.linkedlist.singlelinkedlist;

import java.util.HashMap;

/**
 * @author: lilei
 * @create: 2020-05-30 15:49
 **/
public class SingleLinkedList {

    private int length;
    private Node head;

    public SingleLinkedList() {
        head = null;
    }

    public Object addHead(Object object) {
        Node newHead = new Node(object);
        if (length > 0) {
            newHead.next = head;
        }
        head = newHead;
        length++;
        return object;
    }

    public boolean delete(Object value) {
        if (length == 0) {
            return false;
        }
        Node current = head;
        Node previous = head;
        while (current.data != value) {
            if (current.next == null) {
                return false;
            }
            previous = current;
            current = current.next;
        }
        if (current == head) {
            head = current.next;
        }else{
            previous.next = current.next;
        }
        length--;
        return true;
    }

    public Object find(Object object) {
        Node current = head;
        int ts = length;
        while (ts > 0) {
            if (object == current.data) {
                return current.data;
            }
            current = current.next;
            ts--;
        }
        return null;
    }

    public static class Node{
        private Object data;
        private Node next;

        public Node(Object data) {
            this.data = data;
        }
    }

}
