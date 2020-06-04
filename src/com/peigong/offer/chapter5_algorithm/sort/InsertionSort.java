package com.peigong.offer.chapter5_algorithm.sort;

/**
 * 插入排序
 * @author: lilei
 * @create: 2020-06-04 15:30
 **/
public class InsertionSort {

    public static void main(String[] args) {
        int[] arr = new int[]{3, 6, 4, 7, 13, 65};
        for (int i = 1; i < arr.length; i++) {
            int insertVal = arr[i];
            int index = i - 1;
            while (index >= 0 && insertVal < arr[index]) {
                arr[index + 1] = arr[index];
                index--;
            }
            arr[index + 1] = insertVal;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

}
