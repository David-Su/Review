package com.practice.java;

import com.practice.java.algorithm.search.BinarySearch;
import com.practice.java.algorithm.sort.QuickSort;
import com.practice.java.algorithm.sort.SelectionSort;
import com.practice.java.algorithm.sort.TreeSort;

public class MyClass {

    public static void main(String[] args) {
        sort();
//        search();
    }

    private static void search(){
//        int result = new BinarySearch().search(new int[]{0,55,89,100,101,102,500,600,999},101);
        int result = new BinarySearch().search(new int[]{101},101);
        System.out.println(result);
    }

    private static void sort(){
        int[] array = {5, 9, 8, 18, 1, 0, 4, 700, 666, 999, 1111, 6, 1, 2, 3};

//        new BubbleSort().sort(array);
//        logArray(array);

//        TreeSort treeSort = new TreeSort();
//        TreeSort.TreeNode tree = treeSort.buildTree(array);
//        treeSort.mPrint(tree);

//        SelectionSort selectionSort = new SelectionSort();
//        selectionSort.sort(array);

        QuickSort quickSort = new QuickSort();
        quickSort.sort(array);
        logArray(array);
    }

    private static void logArray(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int value : array) {
            sb.append(value).append(" ");
        }
        System.out.println(sb.toString());
    }

}
