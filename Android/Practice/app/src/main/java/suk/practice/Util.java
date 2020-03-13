package suk.practice;

import android.app.ActivityManager;
import android.view.MotionEvent;

/**
 * @author SuK
 * @time 2020/3/6 14:45
 * @des
 */
public class Util {

    public static String getAction(MotionEvent event){
        String s = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                s ="按下";
            break;
            case MotionEvent.ACTION_MOVE:
                s="移动";
                break;

            case MotionEvent.ACTION_UP:
                s="抬起";
                break;
        }

        return s;
    }
}
