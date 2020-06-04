package com.peigong.offer.chapter4_data_structure.rbtree;

/**
 * 特性
 * 1.每个节点不是红就是黑
 * 2.根节点是黑色
 * 3.每个叶子节点都是黑色，(即NIL节点都是黑色)
 * 4.如果一个节点是红色，那么它的子节点一定是黑色
 * 5.从一个节点其子孙节点的所有路径上包含相同的黑色节点
 * @author: lilei
 * @create: 2020-06-02 11:55
 **/
public class RBTree {

    private RBNode root = null;
    private RBNode nil = new RBNode();

    public static void main(String[] args) {
        RBTree tree = new RBTree();
        tree.insert(10);
        tree.insert(15);
        tree.insert(20);
        tree.insert(17);
        tree.insert(22);
        tree.insert(6);
        tree.insert(31);
        tree.insert(32);
        System.out.println(true);
        tree.remove(20);
        System.out.println(1);
    }

    public void insert(int value) {
        if (root == null) {
            root = new RBNode(value, true);
        }else{
            RBNode n = root;
            RBNode f = null;
            while (n != null) {
                f = n;
                //重复h
                if (n.value == value) {
                    return;
                } else if (value > n.value) {
                    n = n.right;
                }else{
                    n = n.left;
                }
            }
            RBNode node = new RBNode(value);
            if (value > f.value) {
                f.right = node;
            }else{
                f.left = node;
            }
            node.parent = f;
            insertFix(node);
        }
    }

    /**
     * 重构数
     * @param node
     */
    private void insertFix(RBNode node) {
        //需要重新整理红黑树的现象是，父节点是红色
        while (node.parent != null && !node.parent.black) {
            //如果父节点是祖父节点的左节点
            RBNode parent = node.parent;
            RBNode grandParent = parent.parent;
            if (parent == grandParent.left) {
                RBNode uncle = grandParent.right;
                //如果插入节点和父节点都是红色，叔节点是红色，那么将父节点设为黑色，叔节点设为黑色，祖父节点设为红色，将祖父节点设为新的当前节点
                //注意，此处叔叔节点如果为红，则一定不是NIL节点
                if (uncle != null && !uncle.black) {
                    parent.black = true;
                    uncle.black = true;
                    grandParent.black = false;
                    node = grandParent;
                    continue;
                }
                //如果叔节点是黑，且当前节点是右节点
                //注意，此处不用管叔节点是不是NIL节点了，因为到这里叔节点一定不是红，不管是不是NIL节点
                if (node == parent.right) {
                    rotateLeft(parent);
                    node = parent;
                    parent = node.parent;
                }
                parent.black = true;
                grandParent.black = false;
                rotateRight(grandParent);
            }else{
                RBNode uncle = grandParent.left;
                //如果插入节点和父节点都是红色，叔节点是红色，那么将父节点设为黑色，叔节点设为黑色，祖父节点设为红色，将祖父节点设为新的当前节点
                //注意，此处叔叔节点如果为红，则一定不是NIL节点
                if (uncle != null && !uncle.black) {
                    parent.black = true;
                    uncle.black = true;
                    grandParent.black = false;
                    node = grandParent;
                    continue;
                }
                //如果叔节点是黑，且当前节点是右节点
                //注意，此处不用管叔节点是不是NIL节点了，因为到这里叔节点一定不是红，不管是不是NIL节点
                if (node == parent.left) {
                    rotateRight(parent);
                    node = parent;
                    parent = node.parent;
                }
                parent.black = true;
                grandParent.black = false;
                rotateLeft(grandParent);
            }
        }
        // 将根节点设为黑色
        root.black = true;
    }

    /**
     * 左旋
     * @param y
     * 示意图
     *          py                                   py
     *            \                                   \
     *            y                                    x
     *          /   \         --左旋-->                /  \
     *         ly    x                               y    rx
     *              /  \                           /   \
     *             lx   rx                        ly   lx
     */
    private void rotateLeft(RBNode y) {
        //满足左旋条件的、y为根节点的树，一定存在y的右子节点x
        RBNode x = y.right;
        //将x的左子节点设为y的右子节点
        y.right = x.left;
        //如果x的左子节点存在，将其父节点设为y
        if (y.right != null) {
            y.right.parent = y;
        }
        //将x的父节点改为y的父节点
        x.parent = y.parent;
        //如果y的父节点存在，则将y在父节点中对应位置改为x，否则y为root节点，设root节点为x
        if (y.parent != null) {
            if (y.parent.left == y) {
                y.parent.left = x;
            }else{
                y.parent.right = x;
            }
        }else{
            root = x;
        }
        x.left = y;
        y.parent = x;
    }

    /**
     * 右旋
     * @param y 右旋操作的根节点
     * * 右旋示意图(对节点y进行右旋)：
     *             py                               py
     *            /                                /
     *           y                                x
     *          /  \      --右旋-->              /  \
     *         x   ry                          lx   y
     *        / \                                   / \
     *       lx  rx                                rx  ry
     *
     *
     */
    private void rotateRight(RBNode y) {
        //如果达到右旋的标准，则右旋根节点一定存在左子节点x
        //右旋根节点的左节点为x
        RBNode x = y.right;
        //将x的右孩子设为y的左孩子
        y.left = x.right;
        //如果右孩子不为空，那么将它的父节点改为y
        if (y.left != null) {
            y.left.parent = y;
        }
        //将x节点的父节点改为y的父节点
        x.parent = y.parent;
        //如果y的父节点存在，则将y的父节点中对应的y的位置换位x，否则y是root节点，将x设为新的root
        if (y.parent != null) {
            if (y.parent.left ==y) {
                y.parent.left = x;
            }else{
                y.parent.right = x;
            }
        }else{
            root = x;
        }
        x.right = y;
        y.parent = x;
    }

    public void remove(int value) {
        //首先找到要删除的节点
        RBNode cur = root;
        while (cur != null && cur.value != value) {
            if (value > cur.value) {
                cur = cur.right;
            }else{
                cur = cur.left;
            }
        }
        if (cur != null) {
            //删除的节点的颜色
            boolean deleteBlack = false;
            //重新调整树的目标节点
            RBNode target = null;
            if (cur.left == null || cur.right == null) {
                deleteBlack = cur.black;
                if (cur.right != null) {
                    translate(cur,cur.right);
                }else{
                    translate(cur,cur.left);
                }
            }else{
                //两个节点都存在的情况，则用其右子树的最小节点替换它的位置
                RBNode rightMin = findRightMin(cur);
                cur.value = rightMin.value;
                deleteBlack = rightMin.black;
                target = rightMin.right;
                translate(cur,rightMin.right);
            }
            if (deleteBlack) {
                if (target != null && !target.black) {
                    target.black = true;
                }else{
                    removeFix(target);
                }
            }
        }
    }

    private void removeFix(RBNode target) {

    }

    /**
     * 顶位
     * @param source
     * @param target
     */
    private void translate(RBNode source, RBNode target) {
        if (source.parent != null) {
            if (source == source.parent.left) {
                source.parent.left = target;
            }else{
                source.parent.right = target;
            }
        }
        if (target != null) {
            target.parent = source.parent;
        }
    }

    private RBNode findRightMin(RBNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

}
