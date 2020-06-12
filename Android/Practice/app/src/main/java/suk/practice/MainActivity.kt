package suk.practice

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import suk.practice.server.ServerService
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {

    lateinit var subscribe: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getTask()
        externalCacheDir
        btn.setOnClickListener {
            invacation().a("哈哈哈哈哈哈哈")
            Glide.with(this)
                .load(
                    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTBERkMzM0M3MDUxMTFFNzk1NkRCNzg0RkMwMDUzMzkiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTBERkMzM0Q3MDUxMTFFNzk1NkRCNzg0RkMwMDUzMzkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1MERGQzMzQTcwNTExMUU3OTU2REI3ODRGQzAwNTMzOSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1MERGQzMzQjcwNTExMUU3OTU2REI3ODRGQzAwNTMzOSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PjUUOd0AAAKHSURBVHjaVJJdSBRRFMf/d/aOs7szq7huFiuNrLaUkqhkUoJCQR8vgUFZIX08GEiED0H5ID1VEPTxavkkQfaSYCRkEAg+hA9BWGhJoUuirK5K5dbOzM7O6exuPXRh4J4z53fu/f/PFZnnaBUSU+ClKICq5jf4fxHgZoFcjvdCB7mZDmGNgdQari0BPEfDp4SGVOon0pkiE9CAUEhHQ1wgEEqDsm1wvs4ADFreLCj5Nkh3rjdSqHQn96+mQ60ldLTNR0YozHGELp+J0sIkyJsD5ZkCSIugp3eNQkHtrloaG9pHtKQRrUiaGqmh/c1x/mfQ/WvgfBGU/2Soal5YEOsbGays8v2y+ayL9sMJjJtVmJgEDjSx3K1ifV6jpdZC29rSsLCm4c30Dtx67KA6sgSp5CB9Ale7JS72cadl9sHingnYRTAGLe+c4uOkG0QylUH6Nyd8QYxO7ca9wQ+IVkUxNGCh/WAK9izsgvFKGfDslYRo0HCk10COgqhrBOoaXPT3Aj1dZfg8v4rE0nfW9FdbwZwvoLnxMJ07XU/lYT+d3KvT60eCJoYV6jkb4KP9VL8nTu9GI0TzRXOE+xKWjzXOfAQ2vSgeDPuRnFzDsuOi3LAQ4xmbLdvQ1+2hLrYBLw0432BLBQKDT1TcHpRI/XAQKXXg6ApulPpwJeaD0cIvozUFVLAxfLbgUF3fDowMSMuv5ocsSBQeF4g9ohOKoE3FT6Tp5Bp+cpoDlD1mkN3Bs7x0nOTDUYLwHNRUm0wp+MW0uZJEl2NxLJGzMyD2ULwXPNUspB4GKo1OZTlVQecvnJoWPmnqumH6DcPUPc9s6r+5GK6qtG1Gi59jy1i1LeORTsjciz8CDACzHh/U4gnF4AAAAABJRU5ErkJggg==\n"
                )
                .into(iv)
        }
        bindService(Intent(this, ServerService::class.java), object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d("客户端", service?.hashCode().toString())
            }

        }, Context.BIND_AUTO_CREATE)

//        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//        startActivityForResult(intent, 1)

//        if (Settings.canDrawOverlays(this)) {
//            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+packageName))
//            startActivityForResult(intent, 1)
//        } else {
//            addWindow()
//        }
//        addWindow()


        val lock = ReentrantLock()

        thread {
            lock.lock()
            try {

                lock.lock() //可重入
                try {

                } finally {
                    lock.unlock()
                }

            } finally {
                lock.unlock()
            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addWindow() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_launcher_foreground)
        val lp = WindowManager.LayoutParams().apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.START or Gravity.TOP
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            x = 0
            y = 0
        }
        wm.addView(imageView, lp)


        imageView.setOnTouchListener { v, event ->
            var startX = 0
            var startY = 0
            var downX = 0f
            var downY = 0f
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = lp.x
                    startY = lp.y
                    downX = event.rawX
                    downY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = startX + event.rawX - downX
                    val y = startY + event.rawY - downY
                    lp.x = x.toInt()
                    lp.y = y.toInt()

                    wm.updateViewLayout(imageView, lp)
                }
            }
            true
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("MainActivity", "dispatchTouchEvent")
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> addWindow()
        }
    }
}
