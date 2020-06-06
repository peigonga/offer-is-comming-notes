package com.peigong.offer.chapter5_algorithm.sort;

/**
 * @author: lilei
 * @create: 2020-06-05 10:44
 **/
public class ShellSort {

    public static void main(String[] args) {
        int[] arr = new int[]{15, 2, 6, 23, 14, 65, 18};
        shellSort(arr);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    public static void shellSort(int[] arr) {
        for (int incr = arr.length / 2; incr > 0; incr /= 2) {
            for (int i = incr;  i < arr.length ; i++) {
                int j = i;
                while (j - incr >= 0 && arr[j] < arr[j - incr]) {
                    int temp = arr[j];
                    arr[j] = arr[j - incr];
                    arr[j - incr] = temp;
                    j -= incr;
                }
            }
        }
    }

}
