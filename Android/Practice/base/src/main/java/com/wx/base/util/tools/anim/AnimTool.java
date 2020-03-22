package com.wx.base.util.tools.anim;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Author  Administer
 * Time    2017/7/14 9:35
 * Des     ${TODO}
 */

public class AnimTool {

    private static final Object lock = new Object();
    private static volatile AnimTool instance;
    private final long PULL_DURATION = 300L;
    private final String ROTATION = "rotation";

    public static AnimTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AnimTool();
                }
            }
        }
        return instance;
    }

    /**
     * 向下拉伸动画
     *
     * @param view
     */
    public void pullDown(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        getPullAnim(view, 0, view.getMeasuredHeight()).setDuration(PULL_DURATION).start();
    }

    /**
     * 向上收缩动画
     *
     * @param view
     */
    public void pullup(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        getPullAnim(view,view.getMeasuredHeight(),0).setDuration(PULL_DURATION).start();
    }


    /**
     * 收缩或拉伸动画
     *
     * @param view
     */
    public void pull(final View view, final int from, final int to) {
        if (from == to) {
            return;
        }
        getPullAnim(view,from,to).setDuration(PULL_DURATION).start();
    }

    private ValueAnimator getPullAnim(final View view, final int start, final int end) {
        ValueAnimator anim = ValueAnimator.ofInt(1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction(); //获得百分比
                view.getLayoutParams().height = (int) (start + fraction * (end - start)); //获得动态高度
                view.requestLayout();
            }
        });
        return anim;
    }

    /**
     * 旋转动画
     * @param view
     * @param from
     * @param to
     */
    public void rotate(View view, int from, int to) {
        ObjectAnimator.ofFloat(view, ROTATION, from, to)
                .setDuration(200)
                .start();
    }

}
