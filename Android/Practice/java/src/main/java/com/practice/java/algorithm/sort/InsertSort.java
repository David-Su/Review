package com.practice.java.algorithm.sort;


/**
 * @author SuK
 * @time 2020/4/30 14:31
 * @des
 */
public class InsertSort implements Strategy {

    @Override
    public int[] sort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int temp = array[i + 1];
            int index = i;
            while (index >= 0 && array[index] > temp) {
                array[index + 1] = array[index];
                index--;
            }
            array[index + 1] = temp;
        }
        return array;
    }

}
