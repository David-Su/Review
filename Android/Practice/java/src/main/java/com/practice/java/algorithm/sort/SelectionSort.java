package com.practice.java.algorithm.sort;

/**
 * @author SuK
 * @time 2020/4/10 13:58
 * @des
 */
public class SelectionSort implements Strategy {

    //垃圾算法
    //最好情况，最快情况都是：
    //执行次数：n+（n-1）+...+1 = n^2+n
    //时间复杂度：O(n^2)
    public int[] sort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int k = i; k < array.length; k++) {
                if (array[k] < array[minIndex]) {
                    minIndex = k;
                }
            }
            int temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;
        }
        return array;
    }

}
