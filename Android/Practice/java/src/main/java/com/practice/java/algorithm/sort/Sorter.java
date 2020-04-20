package com.practice.java.algorithm.sort;


/**
 * @author SuK
 * @time 2020/4/19 14:12
 * @des
 */
public class Sorter {

    private Strategy mStrategy;

    public Sorter sortStrategy(Strategy strategy) {
        mStrategy = strategy;
        return this;
    }

    public int[] sort(int[] array) {
        int[] temp = new int[array.length];
        System.arraycopy(array, 0, temp, 0, array.length);
        long start = System.currentTimeMillis();
        if (mStrategy != null) {
            temp = mStrategy.sort(temp);
            System.out.println("Sorter " + mStrategy.getClass().getSimpleName() + "排序用时：" + (System.currentTimeMillis() - start));
        }
        return temp;
    }
}
