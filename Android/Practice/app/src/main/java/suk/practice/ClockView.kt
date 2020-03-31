package suk.practice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.icu.util.Measure
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.sin

/**
 * @author SuK
 * @time 2020/3/23 17:48
 * @des
 */
class ClockView : View {


    private val mPaint = Paint()
    private val mTag = "ClockView"
    private var mSecDegress = 0.0

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

            mSecDegress = (second.toDouble() + 1) / 60 * 360

            invalidate()

            Log.d(mTag, second.toString() + "     " + mSecDegress)
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

    val picture = Picture().also {picture->
        val picCanvas = picture.beginRecording(100, 100)
        picCanvas.drawCircle(0f,0f,200f,mPaint)
        picture.endRecording()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = height.toFloat()
        val r = height / 2f
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.strokeWidth = context.resources.getDimension(R.dimen.dp_5)
        mPaint.color = context.getColor(R.color.colorPrimary)
        mPaint.style = Paint.Style.STROKE
        canvas.drawCircle(r, r, r, mPaint)


        val stopX = r + r * cos(Math.toRadians(90 - mSecDegress))
        val stopY = r - r * sin(Math.toRadians(90 - mSecDegress))
        canvas.drawLine(r, r, stopX.toFloat(), stopY.toFloat(), mPaint)
        Log.d(mTag, "r:"+r)
        Log.d(mTag, "degree:"+(90 - mSecDegress))
        Log.d(mTag, "cos:"+cos(90 - mSecDegress))
//        canvas.drawArc(RectF(0f,0f,size,size),0f,360f,true,mPaint)
RectF()
        canvas.drawPicture(picture)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

}