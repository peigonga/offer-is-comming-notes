package com.peigong.offer.chapter4_data_structure.twowaylinkedlist;

/**
 * @author: lilei
 * @create: 2020-05-30 16:14
 **/
public class TwoWayLinkedList<E> {

    private Node<E> head;
    private Node<E> last;
    private int length;

    public TwoWayLinkedList() {
        head = null;
        last = null;
    }

    public void addHead(E e) {
        Node<E> newHead = new Node<>(e);
        if (head == null) {
            head = newHead;
            last = newHead;
        }else{
            head.prev = newHead;
            newHead.next = head;
            head = newHead;
        }
        length++;
    }

    public void addLast(E e) {
        Node<E> newLast = new Node<>(e);
        if (last == null) {
            head = newLast;
            last = newLast;
        }else{
            last.next = newLast;
            newLast.prev = last;
            last = newLast;
        }
        length++;
    }

    public E deleteHead(){
        if (length==0) {
            return null;
        }
        Node<E> temp = head;
        head = head.next;
        head.prev = null;
        length--;
        return temp.data;
    }

    public E deleteLast(){
        if (length==0) {
            return null;
        }
        Node<E> temp = last;
        last = last.prev;
        last.next = null;
        length--;
        return temp.data;
    }

    private static class Node<E>{
        private E data;
        private Node<E> next;
        private Node<E> prev;

        public Node(E e) {
            this.data = e;
        }
    }

}
