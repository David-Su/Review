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
            int temp = array[i + 1]; //这是一个基准数，在它之前的数组是有序的，即index之前的数组是有序的，算法要做的就是把这个基准数插入到index之前的有序数组中。
            int index = i;
            while (index >= 0 && array[index] > temp) {//基准数之前的数字逐个与基准数对比，如果大于基准数则向前移动一位，否则停止循环
                array[index + 1] = array[index];
                index--;
            }
            array[index + 1] = temp;
        }
        return array;
    }

}
