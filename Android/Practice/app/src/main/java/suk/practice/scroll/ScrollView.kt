package suk.practice.scroll

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

/**
 * @author SuK
 * @time 2020/4/13 16:10
 * @des
 */
class ScrollView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mLastX = 0
    private var mLasty = 0
    private val mScroller = Scroller(context)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller.abortAnimation()
                mLastX = event.x.toInt()
                mLasty = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d("ScrollView",(mLasty - event.rawY.toInt()).toString())
//                if (scrollX > width || scrollY > width || scrollX < 0 || scrollY < 0) {
//                    return true
//                }
                scrollBy(mLastX - event.x.toInt(), mLasty - event.y.toInt())
                mLastX = event.x.toInt()
                mLasty = event.y.toInt()
            }
            MotionEvent.ACTION_UP -> {
//                mScroller.startScroll(scrollX,scrollY, (-width - scrollX),0,5000)
//                invalidate()


                ObjectAnimator.ofInt(this, "scrollX", scrollX, -width).apply {
                    duration = 3000
                    interpolator = DecelerateInterpolator()
                }.start()
            }
        }
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText("哈哈哈我在这", 0f, 0f, Paint().apply {
            color = Color.BLACK
        })
    }

}