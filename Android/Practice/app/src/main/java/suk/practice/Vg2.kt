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
class Vg2(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"dispatchTouchEvent  "+Util.getAction(ev))
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"onInterceptTouchEvent  "+Util.getAction(ev))
        return if (ev!!.action == MotionEvent.ACTION_DOWN) true else  super.onInterceptTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("suk","  "+javaClass.simpleName+"    "+"onTouchEvent  "+Util.getAction(event))
        return super.onTouchEvent(event)
    }

}