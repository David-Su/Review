package com.practice.java.algorithm.other;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SuK
 * @date 2023/9/10 15:37
 * @des 有A和B两个有序数组（数组元素不重复），给出sum，请找到A和B中所有相加和为sum的序列对（面头条遇到的）
 * A:[1,4,5]
 * B:[3,4,6]
 * sum=8
 */
public class FindPairsWithSum {
    public static List<int[]> findPairsWithSum(int[] arr1, int[] arr2, int sum) {
        List<int[]> pairs = new ArrayList<>();
        int pointerA = 0;
        int pointerB = arr2.length - 1;

        while (pointerA < arr1.length && pointerB >= 0) {
            int currentSum = arr1[pointerA] + arr2[pointerB];
            if (currentSum == sum) {
                pairs.add(new int[]{arr1[pointerA], arr2[pointerB]});
                pointerA++;
                pointerB--;
            } else if (currentSum < sum) {
                pointerA++;
            } else {
                pointerB--;
            }
        }

        return pairs;
    }

    public static void main(String[] args) {
        int[] arr1 = {1, 4, 5};
        int[] arr2 = {3, 4, 6};
        int sum = 8;

        List<int[]> result = findPairsWithSum(arr1, arr2, sum);

        for (int[] pair : result) {
            System.out.println(pair[0] + ", " + pair[1]);
        }
    }
}
