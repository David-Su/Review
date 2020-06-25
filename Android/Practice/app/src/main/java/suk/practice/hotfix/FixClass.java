package suk.practice.hotfix;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @author SuK
 * @time 2020/6/24 10:51
 * @des
 */
public class FixClass {

    static {
        Log.d("hotfix","类加载");
    }

    public void run(Context context) {
        Toast.makeText(context, "已修复", Toast.LENGTH_SHORT).show();
    }

}
