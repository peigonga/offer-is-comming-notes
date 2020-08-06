package com.peigong.offer.chapter5_algorithm.sort;

/**
 * @author: lilei
 * @create: 2020-06-05 10:44
 **/
public class ShellSort {

    public static void main(String[] args) {
        int[] arr = new int[]{15, 2, 6, 23, 14, 65, 18};
        shellSort2(arr);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    public static void shellSort(int[] arr) {
        //最外层分割原始集合
        for (int incr = arr.length / 2; incr > 0; incr /= 2) {
            //第二层循环控制对每个经最外层循环分割出的子集合分别进行插入排序
            for (int i = incr;  i < arr.length ; i++) {
                //最内部为插入排序的算法，不过集合中元素的间隔为incr
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

    public static void shellSort2(int[] arr) {
        for (int incr = arr.length / 2; incr > 0; incr /= 2) {
            for (int i = incr; i < arr.length; i++) {
                int idx = i;
                while (idx - incr >= 0 && arr[idx] < arr[idx - 1]) {
                    int temp = arr[idx];
                    arr[idx] = arr[idx - 1];
                    arr[idx - 1] = temp;
                    idx -= incr;
                }
            }
        }
    }

}
