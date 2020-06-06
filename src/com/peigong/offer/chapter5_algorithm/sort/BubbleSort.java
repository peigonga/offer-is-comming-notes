package com.peigong.offer.chapter5_algorithm.sort;

/**
 * 冒泡排序
 *
 * @author: lilei
 * @create: 2020-06-04 15:04
 **/
public class BubbleSort {

    public static void main(String[] args) {
        int[] arr = new int[]{4, 5, 6, 3, 2, 1};
        bubbleSort(arr);
        for (int i : arr) {
            System.out.println(i);
        }
        /*
        //可理解为，i代表有几个元素找到了应该待的位置
        for (int i = 0; i < arr.length - 1; i++) {
            //内层循环代表为完成排序的元素范围
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }*/
    }

    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

}
