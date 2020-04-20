package com.practice.java.algorithm.sort;

/**
 * @author SuK
 * @time 2020/4/10 14:34
 * @des
 */
public class QuickSort implements Strategy {

    public int[] sort(int[] array) {
        quickSort(array, 0, array.length - 1);
        return array;
    }

    private void quickSort(int[] array, int start, int end) {
//        if (start >= end) return;
//        int s = start;
//        int e = end;
//        int target = array[s];
//        while (s < e) {
//            while (s < e && array[e] >= target) {
//                e--;
//            }
//            array[s++] = array[e];
//            while (s < e && array[s] <= target) {
//                s++;
//            }
//            array[e--] = array[s];
//        }
//        array[s] = target;
//
//
//        quickSort(array, start, s - 1);
//        quickSort(array, s + 1, end);

        if (start >= end) return;

        final int key = array[start];
        int s = start;
        int e = end;

        while (s != e) {
            while (array[e] >= key && s < e) {
                e--;
            }
            array[s] = array[e];

            while (array[s] <= key && s < e) {
                s++;
            }
            array[e] = array[s];

        }

        array[s] = key;

        quickSort(array, start, s - 1);
        quickSort(array, s + 1, end);
    }

}
