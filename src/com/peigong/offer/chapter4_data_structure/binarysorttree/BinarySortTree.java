package com.peigong.offer.chapter4_data_structure.binarysorttree;

/**
 * @author: lilei
 * @create: 2020-06-01 15:58
 **/
public class BinarySortTree {

    private Node root;

    public static void main(String[] args) {
        BinarySortTree tree = new BinarySortTree();
        tree.insert(5);
        tree.insert(8);
        tree.insert(3);
        tree.insert(20);
        tree.insert(15);
        tree.insert(18);
        tree.insert(10);
        tree.insert(13);
        tree.insert(7);
        System.out.println(true);
        tree.delete(15);
        System.out.println(true);
        tree.delete(8);
        System.out.println(true);
    }

    public void insert(int key) {
        Node p = root;
        Node prev = null;
        //循环找到要被当做父节点的节点
        while (p != null) {
            prev = p;
            //如果要插入的值比当前节点值小，则向左子树找
            if (key < p.getValue()) {
                p = p.getLeft();
            }else if(key > p.getValue()){//如果要插入的值比当前节点的值大，则向右子树找
                p = p.getRight();
            }else{
                return;
            }
        }
        if (root == null) {
            root = new Node(key);
        } else if (key < prev.getValue()) {
            prev.setLeft(new Node(key));
            prev.getLeft().setParent(prev);
        }else{
            prev.setRight(new Node(key));
            prev.getRight().setParent(prev);
        }
        return;
    }

    public void delete(int key) {
        delete(root, key);
    }

    private boolean delete(Node node, int key) {
        if (node == null) {
            return false;
        }
        if (key==node.getValue()) {
            return delete(node);
        } else if (key < node.getValue()) {
            return delete(node.getLeft(), key);
        }else{
            return delete(node.getRight(), key);
        }
    }

    private boolean delete(Node node) {
        //如果没有子节点，则直接删除
        if (node.getLeft() == null && node.getRight() == null) {
            if (node.getValue() > node.getParent().getValue()) {
                node.getParent().setRight(null);
            }else{
                node.getParent().setLeft(null);
            }
        } else if (node.getLeft() == null) {
            if (node.getParent().getValue() < node.getValue()) {
                node.getParent().setRight(node.getRight());
            }else{
                node.getParent().setLeft(node.getRight());
            }
        } else if (node.getRight() == null) {
            if (node.getParent().getValue() < node.getValue()) {
                node.getParent().setRight(node.getLeft());
            }else{
                node.getParent().setLeft(node.getLeft());
            }
        }else{
            if (node.getParent().getValue() < node.getValue()) {
                node.getParent().setRight(node.getRight());
                Node temp = node.getRight();
                while (temp.getLeft() != null) {
                    temp = temp.getLeft();
                }
                temp.setLeft(node.getLeft());
            }else{
                node.getParent().setLeft(node.getLeft());
                Node temp = node.getLeft();
                while (temp.getRight() != null) {
                    temp = temp.getRight();
                }
                temp.setRight(node.getRight());
            }
        }
        return true;

    }

    public Node search(int value) {
        Node current = root;
        while (current != null) {
            if (current.getValue() == value) {
                return current;
            } else if (current.getValue() < value) {
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
        }
        return null;
    }

}
