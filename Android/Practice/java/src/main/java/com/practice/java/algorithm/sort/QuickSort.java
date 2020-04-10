package com.practice.java.algorithm.sort;

/**
 * @author SuK
 * @time 2020/4/10 14:34
 * @des
 */
public class QuickSort {

    public void sort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(int[] array, int start, int end) {
        if (start >= end) return;
        int s = start;
        int e = end;
        int target = array[s];
        while (s < e) {
            while (s < e && array[e] >= target) {
                e--;
            }
            array[s++] = array[e];
            while (s < e && array[s] <= target) {
                s++;
            }
            array[e--] = array[s];
        }
        array[s] = target;


        quickSort(array, start, s - 1);
        quickSort(array, s + 1, end);

    }

}
