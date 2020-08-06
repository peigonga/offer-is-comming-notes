package com.peigong.offer.chapter5_algorithm.sort;

/**
 * @author: lilei
 * @create: 2020-06-22 14:33
 **/
public class SelectSort {

    public static void main(String[] args) {
        int[] arr = {15, 3, 76, 13, 21, 8, 19};
        selectSort(arr);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    public static void selectSort(int[] arr){
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }
            if (min != i) {
                int temp = arr[min];
                arr[min] = arr[i];
                arr[i] = temp;
            }
        }
    }

}
