package com.practice.java.algorithm.sort;

/**
 * @author SuK
 * @time 2020/4/9 14:09
 * @des
 */
public class BubbleSort {

    //执行次数：
    //设数组大小为n，(4n-1)+(4n-2)+...+2+1=4n*(n/2)=(n^2)/2
    //时间复杂度：O(n^2)
    public void sort(int[] array) {
        for (int i = array.length; i > 0; i--) {
            for (int k = 0; k < i - 1; k++) {
                if (array[k] > array[k + 1]) {
                    int temp = array[k];
                    array[k] = array[k + 1];
                    array[k + 1] = temp;
                }
            }
        }
    }

}
