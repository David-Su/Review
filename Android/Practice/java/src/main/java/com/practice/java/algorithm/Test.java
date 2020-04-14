package com.practice.java.algorithm;

import com.practice.java.algorithm.search.BinarySearch;
import com.practice.java.algorithm.sort.QuickSort;
import com.practice.java.algorithm.sort.SelectionSort;
import com.practice.java.algorithm.sort.TreeSort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Test {

    public static void main(String[] args) {
//        sort();
//        search();
//        list();

        String s = "ababab";

        String[] pres = new String[s.length() - 1];
        String[] ends = new String[s.length() - 1];

        for (int i = 1; i < s.length(); i++) {
            pres[i - 1] = s.substring(0, s.length() - i);
            ends[i - 1] = s.substring(i);
        }

        String longestHappy = "";

        for (int i = 0; i < s.length() - 1; i++) {
            String pre = pres[i];
            if (pre.equals(ends[i]) && pre.length() > longestHappy.length()) {
                longestHappy = pre;
                break;
            }
        }

        System.out.println("longestHappy:" + longestHappy);
    }

    private static void search() {
//        int result = new BinarySearch().search(new int[]{0,55,89,100,101,102,500,600,999},101);
        int result = new BinarySearch().search(new int[]{101}, 101);
        System.out.println(result);
    }

    private static void sort() {
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


    private static void list() {
        final Object obj = new Object();
        final ArrayList<Object> arrayList = new ArrayList<>();
        final LinkedList<Object> linkedList = new LinkedList<>();
        Thread array = new Thread(new Runnable() {
            @Override
            public void run() {
                long curr = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    arrayList.add(0, obj);
                }
                System.out.println("arrayList时间 " + (System.currentTimeMillis() - curr));
            }
        });

        Thread linked = new Thread(new Runnable() {
            @Override
            public void run() {
                long curr = System.currentTimeMillis();
                for (int i = 0; i < 10000000; i++) {
                    linkedList.add(0, obj);
                }
                System.out.println("linkedList时间 " + (System.currentTimeMillis() - curr));
            }
        });

        linked.start();
        array.start();

    }

    private static void logArray(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int value : array) {
            sb.append(value).append(" ");
        }
        System.out.println(sb.toString());
    }

}
