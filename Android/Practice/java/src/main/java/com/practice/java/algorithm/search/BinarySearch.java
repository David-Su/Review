package com.practice.java.algorithm.search;

/**
 * @author SuK
 * @time 2020/4/9 16:55
 * @des
 */
public class BinarySearch {

    //只能输入有序数组
    //：
    //最好的情况 运行次数：1 时间复杂度O(1)
    //最坏的情况：设数组长度为n,循环的次数为k。n/2^k=1，k=log2n 时间复杂度O(logn)
    public int search(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;
        int result = -1;

        while (start <= end) {
            int mid = start + (end - start) / 2;
            if (target > array[mid]) {
                start = mid + 1;
            } else if (target < array[mid]) {
                end = mid - 1;
            } else {
                result = mid;
                break;
            }
        }

        return result;
    }

}
