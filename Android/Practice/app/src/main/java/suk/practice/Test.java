package suk.practice;

import android.util.Log;

/**
 * @author SuK
 * @time 2020/3/30 17:40
 * @des
 */
public class Test {

    public int lengthOfLongestSubstring(String s) {
        String result = s.length() == 0 ? "" : String.valueOf(s.charAt(0));
        outer:
        for (int i = 0; i < s.length(); i++) {
            String temp = String.valueOf(s.charAt(i));
            for (int j = i + 1; j < s.length(); j++) {
                temp = temp + s.charAt(j);
//                Log.d("temp:", temp);
                if (!isRepeat(temp)) {
                    if (temp.length() >= result.length()) {
                        result = temp;
                    }
                } else {
                    continue outer;
                }
            }
        }
        return result.length();
    }

    private boolean isRepeat(String date) {
        for (int i = 0; i < date.length(); i++) {
            String target = String.valueOf(date.charAt(i));
            for (int j = i + 1; j < date.length(); j++) {
                if (String.valueOf(date.charAt(j)).equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

}
