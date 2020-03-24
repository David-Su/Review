package suk.practice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.icu.util.Measure
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.log

/**
 * @author SuK
 * @time 2020/3/23 17:48
 * @des
 */
class ClockView : View {


    private val mPaint = Paint()
    private val mTag = "ClockView"
    private var secDegress = 0f

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        context.registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent?) {
//
//            }
//
//        }, IntentFilter(Intent.ACTION_TIME_TICK))

        timer(period = 1000L) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val second = Calendar.getInstance().get(Calendar.SECOND)

            secDegress = second.toFloat() / 60f * 360f

            invalidate()

            Log.d(mTag, second.toString())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size= height.toFloat()
        val r= height/2f
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.strokeWidth = context.resources.getDimension(R.dimen.dp_5)
        mPaint.color = context.getColor(R.color.colorPrimary)
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(RectF(0f,0f,size,size),0f,360f,true,mPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

}