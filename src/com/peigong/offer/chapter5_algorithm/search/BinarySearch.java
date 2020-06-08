package com.peigong.offer.chapter5_algorithm.search;

/**
 * @author: lilei
 * @create: 2020-06-04 14:48
 **/
public class BinarySearch {

    public static void main(String[] args) {
        int[] arr = new int[]{3, 5, 6, 12, 15, 17, 22, 28, 31, 46, 51, 72, 81};
        if (binarySearch(arr, 1)) {
            System.out.println("exists");
        }
    }

    public static boolean binarySearch(int[] arr, int val) {
        int low = 0;
        int high = arr.length - 1;
        int mid ;
        while (low <= high) {
            mid = (high - low) / 2 + low;
            if (arr[mid] == val) {
                return true;
            } else if (arr[mid] > val) {
                high = mid - 1;
            }else{
                low = mid + 1;
            }
        }
        return false;
    }

}
