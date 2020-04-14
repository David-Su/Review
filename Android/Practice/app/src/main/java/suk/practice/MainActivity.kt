package suk.practice

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    lateinit var subscribe:Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getTask()

        btn.setOnClickListener {
            invacation().a("哈哈哈哈哈哈哈")
            Glide.with(this)
                .load("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTBERkMzM0M3MDUxMTFFNzk1NkRCNzg0RkMwMDUzMzkiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTBERkMzM0Q3MDUxMTFFNzk1NkRCNzg0RkMwMDUzMzkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1MERGQzMzQTcwNTExMUU3OTU2REI3ODRGQzAwNTMzOSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1MERGQzMzQjcwNTExMUU3OTU2REI3ODRGQzAwNTMzOSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PjUUOd0AAAKHSURBVHjaVJJdSBRRFMf/d/aOs7szq7huFiuNrLaUkqhkUoJCQR8vgUFZIX08GEiED0H5ID1VEPTxavkkQfaSYCRkEAg+hA9BWGhJoUuirK5K5dbOzM7O6exuPXRh4J4z53fu/f/PFZnnaBUSU+ClKICq5jf4fxHgZoFcjvdCB7mZDmGNgdQari0BPEfDp4SGVOon0pkiE9CAUEhHQ1wgEEqDsm1wvs4ADFreLCj5Nkh3rjdSqHQn96+mQ60ldLTNR0YozHGELp+J0sIkyJsD5ZkCSIugp3eNQkHtrloaG9pHtKQRrUiaGqmh/c1x/mfQ/WvgfBGU/2Soal5YEOsbGays8v2y+ayL9sMJjJtVmJgEDjSx3K1ifV6jpdZC29rSsLCm4c30Dtx67KA6sgSp5CB9Ale7JS72cadl9sHingnYRTAGLe+c4uOkG0QylUH6Nyd8QYxO7ca9wQ+IVkUxNGCh/WAK9izsgvFKGfDslYRo0HCk10COgqhrBOoaXPT3Aj1dZfg8v4rE0nfW9FdbwZwvoLnxMJ07XU/lYT+d3KvT60eCJoYV6jkb4KP9VL8nTu9GI0TzRXOE+xKWjzXOfAQ2vSgeDPuRnFzDsuOi3LAQ4xmbLdvQ1+2hLrYBLw0432BLBQKDT1TcHpRI/XAQKXXg6ApulPpwJeaD0cIvozUFVLAxfLbgUF3fDowMSMuv5ocsSBQeF4g9ohOKoE3FT6Tp5Bp+cpoDlD1mkN3Bs7x0nOTDUYLwHNRUm0wp+MW0uZJEl2NxLJGzMyD2ULwXPNUspB4GKo1OZTlVQecvnJoWPmnqumH6DcPUPc9s6r+5GK6qtG1Gi59jy1i1LeORTsjciz8CDACzHh/U4gnF4AAAAABJRU5ErkJggg==\n")
                .into(iv)
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe.dispose()
    }
}
