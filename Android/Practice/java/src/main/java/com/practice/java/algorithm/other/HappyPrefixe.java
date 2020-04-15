package com.practice.java.algorithm.other;

/**
 * @author SuK
 * @time 2020/4/15 10:41
 * @des 「快乐前缀」是在原字符串中既是 非空 前缀也是后缀（不包括原字符串自身）的字符串。
 * <p>
 * 给你一个字符串 s，请你返回它的 最长快乐前缀。
 * <p>
 * 如果不存在满足题意的前缀，则返回一个空字符串。
 */
public class HappyPrefixe {

    /**
     * @param s ababab
     * @return
     */
    public String get(String s) {
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
        return longestHappy;

    }
}
