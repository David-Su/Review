package suk.practice

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * @author SuK
 * @time 2020/3/6 14:08
 * @des
 */
class Vg1(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"dispatchTouchEvent  "+Util.getAction(ev))
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"onInterceptTouchEvent  "+Util.getAction(ev))
        return super.onInterceptTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"onTouchEvent  "+Util.getAction(event))
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("onMeasure",Thread.currentThread().name)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}