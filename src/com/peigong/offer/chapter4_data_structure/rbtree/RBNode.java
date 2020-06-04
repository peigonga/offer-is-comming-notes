package com.peigong.offer.chapter4_data_structure.rbtree;

/**
 * @author: lilei
 * @create: 2020-06-02 11:53
 **/
public class RBNode {

    int value;
    boolean black;

    RBNode left;
    RBNode right;
    RBNode parent;

    public RBNode() {
    }

    public RBNode(int value) {
        this.value = value;
    }

    public RBNode(int value, boolean black) {
        this.value = value;
        this.black = black;
    }

    public RBNode(int value, boolean black, RBNode parent) {
        this.value = value;
        this.black = black;
        this.parent = parent;
    }
}
