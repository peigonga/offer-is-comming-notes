package com.peigong.offer.chapter5_algorithm.sort;

/**
 * 插入排序
 * @author: lilei
 * @create: 2020-06-04 15:30
 **/
public class InsertionSort {

    public static void main(String[] args) {
        int[] arr = new int[]{3, 6, 4, 7, 13, 65};
        insertionSort(arr);
        /*for (int i = 1; i < arr.length; i++) {
            //从左到右一个一个对比，看当前元素是不是比他左边的小，如果是，则左边的向右移
            //因为第一个元素左边没有其他元素，所以从第二个开始
            int insertVal = arr[i];
            int index = i - 1;
            while (index >= 0 && insertVal < arr[index]) {
                arr[index + 1] = arr[index];
                index--;
            }
            arr[index + 1] = insertVal;
        }*/
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length - 1; i++) {
            int idx = i;
            while (idx - 1 >= 0 && arr[idx] < arr[idx - 1]) {
                int temp = arr[idx];
                arr[idx] = arr[idx - 1];
                arr[idx - 1] = temp;
                idx--;
            }
        }
    }

}
