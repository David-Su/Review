package com.example.image

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import java.io.InputStream
import androidx.core.view.doOnPreDraw
import java.util.concurrent.Flow

/**
 * @Author SuK
 * @Des
 * @Date 2023/9/11
 */
class LongImgView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var decoder: BitmapRegionDecoder? = null
    private var imgSize: Size = Size(0, 0)

    //显示区域
    private var displayRect: Rect = Rect()
    private val decodeOption = BitmapFactory.Options()
    private var lastX: Float? = null
    private var lastY: Float? = null


    fun setInputStream(inputStream: InputStream) {

        val option = BitmapFactory.Options().also {
            it.inJustDecodeBounds = false
        }

        BitmapFactory.decodeStream(inputStream, null, option)

        imgSize = Size(option.outWidth, option.outHeight)

        decoder = BitmapRegionDecoder.newInstance(inputStream, false)

        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x - lastX!!
                val moveY = event.y - lastY!!
                lastX = event.x
                lastY = event.y


                //显示区域最大移动距离
                val maxDx = (imgSize.width - width).coerceAtLeast(0)
                val maxDy = (imgSize.height - height).coerceAtLeast(0)

                val newLeft = (displayRect.left - moveX.toInt()).coerceIn(0, maxDx)
                val newTop = (displayRect.top - moveY.toInt()).coerceIn(0, maxDy)

                displayRect.offsetTo(newLeft, newTop)

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val decoder = this.decoder ?: return
        val bitmap = decoder.decodeRegion(displayRect, decodeOption)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        displayRect =
            Rect(0, 0, width.coerceAtMost(imgSize?.width), height.coerceAtMost(imgSize?.height))
    }

}